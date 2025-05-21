import React from "react";
import SearchForm from "./SearchForm";
import { useStore } from "../../stores";
import { Form, Formik } from "formik";
import LocalConstants from "app/LocalConstants";
import { useHistory } from "react-router-dom";
import ConstantList from "../../appConfig";
import { makeStyles } from "@material-ui/core/styles";

const useStyles = makeStyles(() => ({
  SearchForm: {
    backgroundColor: "#fff",
    boxShadow:
      "0px 2px 4px rgba(0, 0, 0, 0.14), 0px 3px 4px rgba(0, 0, 0, 0.12), 0px 1px 5px rgba(0, 0, 0, 0.2)",
    borderRadius: "10px",
  },
}));

export default function SearchFilterLayout() {
  const { administrativeUnitStore } = useStore();
  const { updatePageData, searchObject, handleSetSearchObject } =
    administrativeUnitStore;
  const history = useHistory();
  const classes = useStyles();

  function hanledFormSubmit(searchObj) {
    const newSearchObject = {
      ...searchObj,
      ...LocalConstants.DEFAULT_PAGINATIONS,
    };
    handleSetSearchObject(newSearchObject);
    updatePageData(newSearchObject);
    if (newSearchObject.org) {
      history.push(
        ConstantList.ROOT_PATH +
          "category/administrative-unit" +
          newSearchObject.org.id
      );
    } else {
      history.push(ConstantList.ROOT_PATH + "category/administrative-unit");
    }
  }

  return (
    <Formik
      initialValues={searchObject}
      onSubmit={(values) => hanledFormSubmit(values)}
    >
      {() => (
        <Form autoComplete="off" className={classes.SearchForm}>
          <SearchForm />
        </Form>
      )}
    </Formik>
  );
}
