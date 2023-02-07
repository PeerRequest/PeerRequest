package com.peerrequest.app.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A DTO to hold paginated data.
 *
 * @param pageSize    number of entities in a page
 * @param currentPage number of the current page (counting start with 1)
 * @param lastPage    number of last page (counting start with 1)
 * @param content     paginated content
 * @param <T>         Type of the paginated content
 */
public record Paged<T>(
    @JsonProperty("page_size") int pageSize,
    @JsonProperty("current_page") int currentPage,
    @JsonProperty("last_page") int lastPage,
    @JsonProperty("content") T content
) {
}
