import React from "react";
import { FormControl } from "@material-ui/core";
import SearchIcon from "@material-ui/icons/Search";
import { useTranslation } from "react-i18next";
import "./SearchBox.scss";

export default function GlobitsSearchInput(props) {
  const { t } = useTranslation();

  const [keyword, setKeyword] = React.useState("");

  const handleKeyDownEnterSearch = (event) => {
    if (event.key === "Enter") {
      var searchObject = {};
      searchObject.keyword = keyword;
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
            searchObject.keyword = keyword;
            props.search(searchObject);
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
