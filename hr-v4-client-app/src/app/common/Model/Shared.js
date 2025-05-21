export class SortPaging {
    empty = null;
    sorted = null;
    unsorted = null;
}

export class PageAble {
    sort = new SortPaging();
    offset = null;
    pageNumber = null;
    pageSize = null;
    unpaged = null;
    paged = null;
}

export class PageResponse {
    content = [];
    pageable = new PageAble();
    last = null;
    totalPages = null;
    totalElements = null;
    size = null;
    number = null;
    sort = new SortPaging();
    first = null;
    numberOfElements = null;
    empty = null;
};