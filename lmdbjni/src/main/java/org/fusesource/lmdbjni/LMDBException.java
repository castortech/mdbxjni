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

package org.fusesource.lmdbjni;

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class LMDBException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public enum Status {
        OK(0, "OK"),
        EINVAL(JNI.EINVAL, ""),
        EACCES(JNI.EACCES, ""),
        ENOENT(JNI.ENOENT, ""),
        EAGAIN(JNI.EAGAIN, ""),
        KEYEXIST(JNI.MDB_KEYEXIST, ""),
        NOTFOUND(JNI.MDB_NOTFOUND, ""),
        PAGE_NOTFOUND(JNI.MDB_PAGE_NOTFOUND, ""),
        CORRUPTED(JNI.MDB_CORRUPTED, ""),
        PANIC(JNI.MDB_PANIC, ""),
        VERSION_MISMATCH(JNI.MDB_VERSION_MISMATCH, ""),
        INVALID(JNI.MDB_INVALID, ""),
        MAP_FULL(JNI.MDB_MAP_FULL, ""),
        DBS_FULL(JNI.MDB_DBS_FULL, ""),
        READERS_FULL(JNI.MDB_READERS_FULL, ""),
        TLS_FULL(JNI.MDB_TLS_FULL, ""),
        TXN_FULL(JNI.MDB_TXN_FULL, ""),
        CURSOR_FULL(JNI.MDB_CURSOR_FULL, ""),
        PAGE_FULL(JNI.MDB_PAGE_FULL, ""),
        MAP_RESIZED(JNI.MDB_MAP_RESIZED, ""),
        INCOMPATIBLE(JNI.MDB_INCOMPATIBLE, ""),
        BAD_RSLOT(JNI.MDB_BAD_RSLOT, "");

        private final int code;
        private final String reason;

        Status(final int statusCode, final String reasonPhrase) {
            this.code = statusCode;
            this.reason = reasonPhrase;
        }

        /**
         * Get the associated status code
         * @return the status code
         */
        public int getStatusCode() {
            return code;
        }

        /**
         * Get the reason phrase
         * @return the reason phrase
         */
        public String getReasonPhrase() {
            return toString();
        }

        /**
         * Get the reason phrase
         * @return the reason phrase
         */
        @Override
        public String toString() {
            return reason;
        }

        /**
         * Convert a numerical status code into the corresponding Status
         * @param statusCode the numerical status code
         * @return the matching Status or null is no matching Status is defined
         */
        public static Status fromStatusCode(final int statusCode) {
            for (Status s : Status.values()) {
                if (s.code == statusCode) {
                    return s;
                }
            }
            return null;
        }
    }
    
    int errorCode;

    public LMDBException() {
    }

    public LMDBException(String message) {
        super(message);
    }

    public LMDBException(String message, int errorCode) {
        super(message + ",rc:" + (Status.fromStatusCode(errorCode) != null ? Status.fromStatusCode(errorCode).name() : errorCode));
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getErrorMessage() {
    	Status status = Status.fromStatusCode(getErrorCode());
        return getMessage() + ",rc:" + (status != null ? status.name() : getErrorCode());
    }
}
