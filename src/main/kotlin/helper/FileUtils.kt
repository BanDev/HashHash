/**

HashHash
Copyright (C) 2023 Russell Banks

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

object FileUtils {
    fun getFormattedBytes(bytes: Long): String {
        // Check for a simple case when bytes are less than 1024
        if (bytes < 1024) return "$bytes B"

        // Define an array of unit prefixes
        val unitPrefixes = arrayOf(" ", "K", "M", "G", "T", "P", "E")

        // Calculate the unit index by dividing the number of bits needed for the bytes value by 10
        // The 'coerceAtMost' function ensures the index stays within the bounds of the 'unitPrefixes' array
        val unitIndex = ((63 - bytes.countLeadingZeroBits()) / 10).coerceAtMost(unitPrefixes.lastIndex)

        // Calculate the value with a corresponding unit index
        val value = bytes.toDouble() / (1L shl unitIndex * 10)

        // Format the result string using the value and unit prefix
        return "%.1f %sB".format(value, unitPrefixes[unitIndex])
    }
}
