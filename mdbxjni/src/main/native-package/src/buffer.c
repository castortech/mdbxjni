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

int mdbx_put_multiple(MDBX_txn *txn, MDBX_dbi dbi, const MDBX_val *key, MDBX_val *data1, MDBX_val *data2, 
		MDBX_put_flags_t flags) {
	MDBX_val data[2] = {
	{ data1->iov_base, data1->iov_len },
	{ data2->iov_base, data2->iov_len }
	};

	return mdbx_put(txn, dbi, key, &data[0], flags);
}

char* map_printf(char *buf, int size, const char * format, void *args) {
	static char buffer[8096];
	int buflen = vsprintf(buffer,format, args);
	buffer[buflen] = '\0';
	strncpy(buf, buffer, size);
	return buf;
}

int ptr_2_cursor(MDBX_cursor * ptr, MDBX_cursor * cursor, size_t bytes) {
	memcpy(cursor, ptr, bytes);
	return 0;
}

int get_mdbx_build_info(void *arg, size_t bytes) {
/*
printf("mdbx_copy version %d.%d.%d.%d\n"
       " - source: %s %s, commit %s, tree %s\n"
       " - build: %s for %s by %s\n"
       " - flags: %s\n"
       " - options: %s\n",
       mdbx_version.major, mdbx_version.minor, mdbx_version.release,
       mdbx_version.revision, mdbx_version.git.describe,
       mdbx_version.git.datetime, mdbx_version.git.commit,
       mdbx_version.git.tree, mdbx_build.datetime,
       mdbx_build.target, mdbx_build.compiler, mdbx_build.flags,
       mdbx_build.options);
//	const char * datetime = mdbx_build.datetime;
	const struct __declspec(dllimport) MDBX_build_info my_build = mdbx_build; 
//	memcpy(mdbx_build.datetime, arg, bytes);
*/
	return MDBX_SUCCESS;
}
