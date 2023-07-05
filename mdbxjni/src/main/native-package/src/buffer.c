 /**
 * Copyright (C) 2013, RedHat, Inc.
 *
 *    http://www.redhat.com/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "mdbxjni.h"

void buffer_copy(const void *source, size_t source_pos, void *dest, size_t dest_pos, size_t length) {
	memmove(((char *)dest)+dest_pos, ((const char *)source)+source_pos, length);
}

void map_val(MDBX_val *in, MDBX_val *out) {
	out->iov_base = in->iov_base;
	out->iov_len = in->iov_len;
}

char* map_printf(char *buf, int size, const char * format, va_list args) {
	static char buffer[1024];
	int buflen = vsprintf(buffer,format, args);
	buffer[buflen] = '\0';
	strncpy(buf, buffer, size);
	return buf;
}

int ptr_2_cursor(MDBX_cursor * ptr, MDBX_cursor * cursor, size_t bytes) {
	memcpy(cursor, ptr, bytes);
	return 0;
}