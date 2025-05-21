export class SearchObject {
    pageIndex = 1;
    pageSize = 10;
    keyword = null;

    constructor(isValueUrl, object) {
        if (isValueUrl) {
            const value = SearchObject.getDataSearchFromUrl();
            Object.assign(this, value);
        }
        Object.assign(this, object);
    }

    static checkSearchObject = (oldValue, newValue) => {
        const hasPageIndex = "pageIndex" in newValue;
        const hasPageSize = "pageSize" in newValue;
        if ((hasPageIndex && hasPageSize) || (!hasPageIndex && !hasPageSize)) {
            oldValue = Object.assign(oldValue, newValue, { pageSize: oldValue.pageSize, pageIndex: 1 })
        } else if (hasPageIndex) {
            oldValue.pageIndex = newValue.pageIndex;
        } else if (hasPageSize) {
            oldValue.pageSize = newValue.pageSize;
            oldValue.pageIndex = 1;
        }

        return oldValue;
    }

    static getTextSearchUrl(value) {
        let pathName = window.location.pathname || "";
        if (pathName.endsWith("/", pathName.length)) {
            pathName = pathName.slice(0, pathName.length)
        }

        pathName += "?";

        Object.keys(value).forEach(key => {
            if (Boolean(value[key])) {
                pathName += `${key}=${value[key]}&`;
            }
        });

        pathName = pathName.slice(0, pathName.length - 1)

        return pathName;
    }

    static pushSearchToUrl(obj) {
        const url = new URL(window.location.href);

        Object.keys(obj).forEach(key => {
            if (Boolean(obj[key])) {
                url.searchParams.set(key, obj[key]);
            }
        });

        return url;
    }

    static getDataSearchFromUrl() {
        const params = new URLSearchParams(window.location.search);
        const searchObject = {};

        for (const key of params.keys()) {
            let value = params.get(key);

            if (value) {
                if (typeof value !== "number" && Number(value) && !isNaN(Number(value))) {
                    value = Number(value);
                }

                if (value === "true") {
                    value = true;
                }

                searchObject[key] = value;
            }
        }


        return searchObject;
    }
}

export class SearchStaff extends SearchObject {
    // tab = 2;
    tab = null;
    civilServantTypeId = null;
    employeeStatusId = null;
    departmentId = null;
    keyword = null;

    constructor(object) {
        super(false, object);
        const value = SearchObject.getDataSearchFromUrl();
        Object.assign(this, value)

        this.employeeStatusId = SearchStaff.EmployeeStatus[this.tab]
    }

    static EmployeeStatus = {
        1: null,
        2: "0ae6592b-1f10-434d-a882-9b95d3d1860b",
        3: "7406c29f-5e4b-4cbd-9844-527d1ac466d6",
        4: "07c380ca-39ce-4d00-9632-e57413b6ca70"
    }
}