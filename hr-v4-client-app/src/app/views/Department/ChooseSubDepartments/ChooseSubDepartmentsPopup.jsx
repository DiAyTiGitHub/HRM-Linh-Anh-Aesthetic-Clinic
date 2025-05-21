import React, { useState, useEffect, memo, useMemo } from "react";
import { Formik, Form, Field, useFormikContext } from "formik";
import { Grid, DialogActions, Button, DialogContent, makeStyles, Radio } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsPopup from "app/common/GlobitsPopup";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsTable from "app/common/GlobitsTable";
import {
    IconButton,
    Icon
} from "@material-ui/core";
import SearchIcon from '@material-ui/icons/Search';
import ListSubDepartmentsInPopup from "./ListSubDepartmentsInPopup";
import SearchSubDepartmentsInPopup from "./SearchSubDepartmentsInPopup";

function ChooseSubDepartmentsPopup(props) {
    const { departmentStore } = useStore();
    const {
        openChooseSubDpmPopup: openPopup,
        setOpenChooseSubDpmPopup,
        pagingSubDeparments,
        resetStoreSubDepartments
    } = departmentStore;

    function handleClosePopup() {
        setOpenChooseSubDpmPopup(false);
    }

    const { t } = useTranslation();

    const { values, setFieldValue } = useFormikContext();

    const [chosenList, setChosenList] = useState(values?.children || []);
    function handleSaveChosenList() {
        setFieldValue("children", [...chosenList]);
        handleClosePopup();
    }

    useEffect(() => {
        if (openPopup) {
            resetStoreSubDepartments();
            pagingSubDeparments();
        }
        // return () => {
        //     resetStoreSubDepartments();
        // };
    }, [openPopup]);

    return (
        <GlobitsPopup
            scroll={"body"}
            size="md"
            open={openPopup}
            noDialogContent
            title={"Chọn các đơn vị con thuộc đơn vị"}
            onClosePopup={handleClosePopup}
        >
            <div className="dialog-body"
            >
                <DialogContent className="p-12">

                    <Grid container spacing={2}>
                        <Grid item xs={12}>
                            <SearchSubDepartmentsInPopup
                            />
                        </Grid>

                        <Grid item xs={12}>
                            <ListSubDepartmentsInPopup
                                chosenList={chosenList}
                                setChosenList={setChosenList}
                            />
                        </Grid>
                    </Grid>
                </DialogContent>
            </div>


            <div className="dialog-footer py-8">
                <DialogActions className="p-0">
                    <div className="flex flex-space-between flex-middle">
                        <Button
                            startIcon={<BlockIcon />}
                            variant="contained"
                            className="btn mr-12 btn-gray d-inline-flex"
                            // color="secondary"
                            type="button"
                            onClick={handleClosePopup}
                        >
                            Đóng
                        </Button>

                        <Button
                            startIcon={<SaveIcon />}
                            className="mr-0 btn btn-success d-inline-flex"
                            variant="contained"
                            type="button"
                            onClick={handleSaveChosenList}
                        >
                            {t("general.button.save")}
                        </Button>
                    </div>
                </DialogActions>
            </div>

        </GlobitsPopup>
    );
}

export default memo(observer(ChooseSubDepartmentsPopup));