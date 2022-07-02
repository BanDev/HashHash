/**

HashHash
Copyright (C) 2022  Russell Banks

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

 */

package api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.russellbanks.HashHash.BuildConfig
import data.GitHubData
import helper.GitHub
import io.klogging.Klogging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.until
import kotlinx.serialization.json.Json

class Ktor : Klogging {

    var httpResponse: HttpResponse? = null
    var githubData: GitHubData? = null
    var retrievedGitHubData = false
    var httpClient: HttpClient? = null
    var checkingGitHubAPI by mutableStateOf(false)
    var lastChecked: Instant? by mutableStateOf(null)

    @Composable
    fun retrieveGitHubData() {
        if (retrievedGitHubData) { return }
        retrievedGitHubData = true
        httpClient = createHttpClient().also { client ->
            rememberCoroutineScope { Dispatchers.IO }.launch(Dispatchers.Default) {
                httpResponse = client.get(GitHub.HashHash.API.latest).also {
                    if (it.status == HttpStatusCode.OK) githubData = it.body()
                }
                lastChecked = Clock.System.now()
            }
        }
    }

    @Composable
    private fun createHttpClient(): HttpClient {
        val scope = rememberCoroutineScope { Dispatchers.IO }
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        scope.launch(Dispatchers.Default) { this@Ktor.logger.info(message) }
                    }
                }
                level = LogLevel.INFO
            }
        }
    }

    fun checkForHashHashUpdate(scope: CoroutineScope) {
        if (!checkingGitHubAPI) {
            scope.launch {
                httpClient?.run {
                    checkingGitHubAPI = true
                    get(GitHub.HashHash.API.latest).also { response ->
                        httpResponse = response
                        if (response.status == HttpStatusCode.OK) {
                            scope.launch(Dispatchers.Default) {
                                githubData = response.body()
                            }
                        }
                    }
                    lastChecked = Clock.System.now()
                    checkingGitHubAPI = false
                }
            }
        }
    }

    fun isUpdateAvailable() = githubData?.tagName?.contains(BuildConfig.appVersion) == false

    fun getReleasedVersion() = githubData?.tagName?.replace("v", "")

    fun getUpdateResponseText(): String {
        return when {
            checkingGitHubAPI -> "Checking"
            githubData?.tagName?.contains(BuildConfig.appVersion) == true -> "You have the latest version"
            githubData?.tagName?.contains(BuildConfig.appVersion) == false -> {
                "Out of date. Latest version is ${githubData?.tagName?.removePrefix("v")}"
            }
            (httpResponse?.headers?.get("X-RateLimit-Remaining")?.toInt() ?: -1) == 0 -> {
                val epochSeconds = httpResponse?.headers?.get("X-RateLimit-Reset")?.toLong()
                val instantEpochSeconds = epochSeconds?.let { Instant.fromEpochSeconds(it) }
                var minutesLeft = instantEpochSeconds?.let { Clock.System.now().until(it, DateTimeUnit.MINUTE) }
                if (minutesLeft == 0L) minutesLeft = 1L
                "Rate limited. Check back in $minutesLeft minute${if (minutesLeft != 1L) "s" else ""}"
            }
            httpResponse != null -> "${httpResponse?.status} - Check back later"
            else -> "Error accessing GitHub API"
        }
    }

}
