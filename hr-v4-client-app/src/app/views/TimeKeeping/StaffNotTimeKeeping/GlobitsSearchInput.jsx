import React from "react";
import { FormControl } from "@material-ui/core";
import SearchIcon from "@material-ui/icons/Search";
import { useTranslation } from "react-i18next";
import "../../../common/SearchBox.scss";

export default function GlobitsSearchInput(props) {
  const { t } = useTranslation();

  const [keyword, setKeyword] = React.useState("");

  const { search, initialValues } = props;

  const handleKeyDownEnterSearch = (event) => {
    if (event.key === "Enter") {
      var searchObject = {};
      searchObject.keyWord = keyword;
      searchObject.workingDate = initialValues?.date;
      props.search(searchObject);
    }
  };

  return (
    <FormControl fullWidth>
      <div className="search-box">
        <input
          onChange={(event) => setKeyword(event.target.value)}
          onKeyPress={handleKeyDownEnterSearch}
          placeholder={t("general.enterSearch")}
        />
        <button
          className="btn btn-search"
          onClick={() => {
            let searchObject = {};
            searchObject.keyWord = keyword;
            searchObject.workingDate = initialValues?.date;
            search(searchObject);
          }}
        >
          <SearchIcon
            style={{
              position: "absolute",
              top: "4px",
              right: "3px",
            }}
          />
        </button>
      </div>
    </FormControl>
  );
}
