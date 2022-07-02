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

package helper

object GitHub {
    private const val githubWebsite = "https://github.com"
    private const val APIWebsite = "https://api.github.com"

    object HashHash {
        object API {
            const val latest = "$APIWebsite/repos/russellbanks/HashHash/releases/latest"
        }
        object Repository {
            const val website = "$githubWebsite/russellbanks/HashHash"
            const val releases = "$website/releases"
            const val newIssue = "$website/issues/new/choose"
        }
    }

}
