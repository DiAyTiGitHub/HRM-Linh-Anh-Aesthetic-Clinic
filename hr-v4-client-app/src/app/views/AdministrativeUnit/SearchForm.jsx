import React, { useEffect } from "react";
import { useFormikContext } from "formik";
import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import { useStore } from "../../stores";
import { useTheme } from "@material-ui/core/styles";
import LocalConstants from "app/LocalConstants";

import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import Search from "@material-ui/icons/Search";
import BlockIcon from "@material-ui/icons/Block";
import DeleteIcon from "@material-ui/icons/Delete";
import AddIcon from "@material-ui/icons/Add";
import NoteIcon from "@material-ui/icons/Note";
import IconButton from "@material-ui/core/IconButton";
import Collapse from "@material-ui/core/Collapse";
import FilterListIcon from "@material-ui/icons/FilterList";

import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";

import {
  pagingAdministratives,
} from "./AdministrativeUnitService";


export default observer(function SearchForm() {
  const { administrativeUnitStore } = useStore();
  const {
    updatePageData,
    handleSetSearchObject,
    setShouldOpenImportDialog,
    handleEditAdministrative,
    handleDeleteList,
    selectedAdministrativeUnitList,
  } = administrativeUnitStore;

  const { setValues, values, setFieldValue } = useFormikContext();

  const { t } = useTranslation();

  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  const isExtraSmall = useMediaQuery(theme.breakpoints.down("xs"));

  const resetObject = {
    keyword: "",
    idNumber: "",
    currentProvince: null,
    currentDistrict: null,
    currentWard: null,
    level: null,
    parent: null,
  };



  useEffect(() => {
    handleChangeProvince();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [values?.currentProvince]);

  useEffect(() => {
    handleChangeDictrict();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [values?.currentDistrict]);

  const handleChangeProvince = () => {
    setFieldValue("currentDistrict", null);
    setFieldValue("currentWard", null);
  };

  const handleChangeDictrict = () => {
    setFieldValue("currentWard", null);
  };

  const [open, setOpen] = React.useState(false);

  function handleResetForm() {
    handleSetSearchObject({
      keyword: "",
    });
  }

  return (
    <>
      <DialogContent className="dialog-body">
        <Grid container spacing={2} alignItems="center">
          <Grid item md={6} sm={4} xs={12}>
            <Grid container>
              {!isExtraSmall && (
                <Grid item xs="auto">
                  <Button
                    className="mr-16 btn btn-primary d-inline-flex"
                    startIcon={<AddIcon />}
                    variant="contained"
                    onClick={() => {
                      handleEditAdministrative();
                    }}
                  >
                    {!isMobile && t("general.button.add")}
                  </Button>

                  {selectedAdministrativeUnitList.length > 0 && (
                    <Button
                      className="mr-36 btn btn-warning d-inline-flex"
                      variant="contained"
                      startIcon={<DeleteIcon />}
                      onClick={() => {
                        handleDeleteList();
                      }}
                    >
                      {!isMobile && t("general.button.delete")}
                    </Button>
                  )}
                  <Button
                    className="btn btn-danger d-inline-flex"
                    startIcon={<NoteIcon />}
                    variant="contained"
                    onClick={() => {
                      setShouldOpenImportDialog(true);
                    }}
                  >
                    {!isMobile && t("general.button.importExcel")}
                  </Button>
                </Grid>
              )}
              {isExtraSmall && (
                <Grid item xs={12}>
                  <Grid container spacing={2}>
                    <Grid item xs={6}>
                      <Button
                        className="btn btn-primary d-inline-flex"
                        startIcon={<AddIcon />}
                        variant="contained"
                        onClick={() => {
                          handleEditAdministrative();
                        }}
                        fullWidth
                      >
                        {t("general.button.add")}
                      </Button>
                    </Grid>
                    {selectedAdministrativeUnitList.length > 0 && (
                      <Button
                        className="mr-36 btn btn-warning d-inline-flex"
                        variant="contained"
                        startIcon={<DeleteIcon />}
                        onClick={() => {
                          handleDeleteList();
                        }}
                      >
                        {t("general.button.delete")}
                      </Button>
                    )}
                    <Grid item xs={6}>
                      <Button
                        className="btn btn-danger d-inline-flex"
                        startIcon={<NoteIcon />}
                        variant="contained"
                        onClick={() => {
                          setShouldOpenImportDialog(true);
                        }}
                        fullWidth
                      >
                        {t("general.button.importExcel")}
                      </Button>
                    </Grid>
                  </Grid>
                </Grid>
              )}
            </Grid>
          </Grid>
          <Grid item xs>
            <GlobitsTextField
              name="keyword"
              variant="outlined"
              notDelay
              InputProps={{
                endAdornment: (
                  <IconButton className="p-0" aria-label="search" type="submit">
                    <Search />
                  </IconButton>
                ),
              }}
            />
          </Grid>
          <Grid item xs="auto">
            <Button
              className="m-0 mr-16 btn btn-primary d-inline-flex"
              startIcon={<FilterListIcon style={{ padding: "3px 0" }} />}
              variant="contained"
              onClick={() => {
                setOpen((prev) => !prev);
              }}
            >
              {!isMobile && t("general.button.filter")}
            </Button>
          </Grid>

          <Grid item xs={12}>
            <Collapse in={open}>
              <Grid container spacing={2} className="pt-10">
                <Grid item md={3} sm={6} xs={12}>
                  <GlobitsSelectInput
                    label={t("administrativeUnit.levelUnit")}
                    name="level"
                    keyValue="value"
                    options={LocalConstants.AdminitractiveLevel}
                    variant="outlined"
                  />
                </Grid>

                <Grid item md={3} sm={6} xs={12}>
                  <GlobitsPagingAutocompleteV2
                    label={t("humanResourcesInformation.province")}
                    name="currentProvince"
                    api={pagingAdministratives}
                    searchObject={{ level: 3 }}
                    onChange={() => {
                      handleChangeProvince();
                    }}
                  />
                </Grid>
                <Grid item md={3} sm={6} xs={12}>
                  <GlobitsPagingAutocompleteV2
                    label={t("humanResourcesInformation.district")}
                    name="currentDistrict"
                    api={pagingAdministratives}
                    searchObject={{
                      level: 4,
                      parentId: values?.currentProvince?.id,
                    }}
                    allowLoadOptions={!!values?.currentProvince?.id}
                    clearOptionOnClose
                    onChange={() => {
                      handleChangeDictrict();
                    }}
                  />
                </Grid>
                <Grid item md={3} sm={6} xs={12}>
                  {/* <GlobitsAutocomplete
                    name="currentWard"
                    label={t("administrativeUnit.wards")}
                    options={listWard ? listWard : []}
                    variant="outlined"
                  /> */}

                  <GlobitsPagingAutocompleteV2
                    label={t("humanResourcesInformation.wards")}
                    name="currentWard"
                    api={pagingAdministratives}
                    searchObject={{
                      level: 5,
                      parentId: values?.currentDistrict?.id,
                    }}
                    allowLoadOptions={!!values?.currentDistrict?.id}
                    clearOptionOnClose
                  />
                </Grid>
                <Grid item md={12} sm={12} xs={12}>
                  <DialogActions className="dialog-footer-new p-0 btn-space-around">
                    {!isExtraSmall && (
                      <Grid container justifyContent="flex-end">
                        <Grid item>
                          <Button
                            startIcon={<BlockIcon />}
                            variant="contained"
                            className="mr-12 btn btn-secondary d-inline-flex"
                            color="secondary"
                            onClick={() => {
                              setValues(resetObject);
                              handleResetForm(resetObject);
                              updatePageData(resetObject);
                            }}
                          >
                            {!isMobile && t("general.button.reset")}
                          </Button>
                        </Grid>

                        <Grid item>
                          <Button
                            startIcon={<Search />}
                            className="mr-0 btn btn-primary d-inline-flex"
                            variant="contained"
                            color="primary"
                            type="submit"
                          // disabled={isSubmitting}
                          >
                            {!isMobile && t("general.button.search")}
                          </Button>
                        </Grid>
                      </Grid>
                    )}
                    {isExtraSmall && (
                      <Grid container spacing={2}>
                        <Grid item xs={6}>
                          <Button
                            startIcon={<BlockIcon />}
                            variant="contained"
                            className="btn btn-secondary d-inline-flex"
                            color="secondary"
                            fullWidth
                            onClick={() => {
                              setValues(resetObject);
                              handleResetForm(resetObject);
                              updatePageData(resetObject);
                            }}
                          >
                            {!isMobile && t("general.button.reset")}
                          </Button>
                        </Grid>

                        <Grid item xs={6}>
                          <Button
                            startIcon={<Search />}
                            className="mr-0 btn btn-primary d-inline-flex"
                            variant="contained"
                            fullWidth
                            color="primary"
                            type="submit"
                          // disabled={isSubmitting}
                          >
                            {!isMobile && t("general.button.search")}
                          </Button>
                        </Grid>
                      </Grid>
                    )}
                  </DialogActions>
                </Grid>
              </Grid>
            </Collapse>
          </Grid>
        </Grid>
      </DialogContent>
    </>
  );
});
