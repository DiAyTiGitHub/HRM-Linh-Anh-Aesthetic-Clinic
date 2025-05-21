import React, { memo, useState } from "react";
import { useStore } from "../../stores";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import SearchIcon from "@material-ui/icons/Search";
import FilterList from "@material-ui/icons/FilterList";
import {
    Collapse,
    Grid,
    Button,
    makeStyles,
    FormControl,
} from "@material-ui/core";
import { Formik, Form, Field } from "formik";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { useParams } from "react-router";

const useStyles = makeStyles((theme) => ({
    filterContainer: {
        position: "relative",
    },
    filterPopup: {
        position: "absolute",
        top: "50px",
        zIndex: "99",
        background: "rgb(244, 245, 247)",
        right: "0",
        boxShadow: "var(--elevation-z8)",
        padding: "0!important",
        "& .MuiCollapse-wrapper": {
            padding: "8px",
        },
    },
    filterBtn: {
        position: "absolute",
        right: "40px",
        top: "4px",
        color: "#01c0c8",
        transition: " 0.2s",
        "&:hover": {
            cursor: "pointer",
            color: "#2086AA",
        },
    },
    popupBtn: {
        background: "#edf1f5",
        textAlign: "center",
        width: "100%",
        padding: "10px 0",
    },
}));

function ProjectActivityFilter(props) {
    const classes = useStyles();
    const { t } = useTranslation();
    const { taskStore } = useStore();
    const { id: projectId } = useParams();
    const { projectActivityStore } = useStore();

    const {
        handleSearching,
    } = projectActivityStore;

    const [showFilter, setShowFilter] = useState(false);
    const [searchObj, setSearchObj] = useState({
        projectId: projectId,
        includeAll: true,
        fromDate: null,
        toDate: null
    });

    function handleSearchActivity(values) {
        const searchObject = {
            ...values,
            projectId: projectId,
            includeAll: true,
        };

        handleSearching(searchObject);
        // pagingProjectActivity(searchObject); 
    }

    return (
        <div className="filter-card" elevation={6}>
            <Formik
                initialValues={searchObj}
                onSubmit={(values) => handleSearchActivity(values)}
            >
                {({ resetForm, values }) => {
                    return (
                        <Form autoComplete="off">
                            <Grid className={classes.filterContainer} container spacing={3}>
                                <Grid item xs={12}>
                                    <FormControl fullWidth>
                                        <div className="search-box input-search-box">
                                            <Field
                                                name="keyword"
                                                placeholder={t("general.enterSearch")}
                                            />
                                            <FilterList
                                                className={classes.filterBtn}
                                                onClick={() => setShowFilter(!showFilter)}
                                            />
                                            <button
                                                className="btn btn-search"
                                                type="submit"
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
                                </Grid>

                                <Grid className={classes.filterPopup} item xs={12}>
                                    <Collapse in={showFilter}>
                                        <Grid container spacing={2}>
                                            <Grid item xs={12}>
                                                <GlobitsDateTimePicker
                                                    label={"Ngày bắt đầu"}
                                                    name="fromDate"
                                                />
                                            </Grid>

                                            <Grid item xs={12}>
                                                <GlobitsDateTimePicker 
                                                    label={"Ngày kết thúc"}
                                                    name="toDate"
                                                />
                                            </Grid>

                                            <div className={classes.popupBtn}>
                                                <Grid item xs={12}>
                                                    <Button
                                                        className="mr-8 btn btn-secondary d-inline-flex"
                                                        variant="contained"
                                                        color="primary"
                                                        onClick={() => resetForm()}
                                                    >
                                                        {t("general.button.reset")}
                                                    </Button>

                                                    <Button
                                                        className="btn btn-primary d-inline-flex"
                                                        variant="contained"
                                                        color="primary"
                                                        type="submit"
                                                    >
                                                        {t("general.button.search")}
                                                    </Button>
                                                </Grid>
                                            </div>
                                        </Grid>
                                    </Collapse>
                                </Grid>
                            </Grid>
                        </Form>
                    );
                }}
            </Formik>
        </div>
    );
}

export default memo(observer(ProjectActivityFilter));
