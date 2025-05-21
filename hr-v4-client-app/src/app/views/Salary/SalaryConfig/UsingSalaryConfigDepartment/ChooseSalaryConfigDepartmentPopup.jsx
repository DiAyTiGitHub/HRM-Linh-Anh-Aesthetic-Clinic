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
import GlobitsSearchInput from "app/common/GlobitsSearchInput";
import ChooseListDepartmentInSalaryConfig from "./ChooseListDepartmentInSalaryConfig";


function ChooseSalaryConfigDepartmentPopup(props) {
    const { departmentStore } = useStore();
    const {
        updatePageData,

    } = departmentStore;
    const {
        openPopup,
        handleClosePopup
    } = props;

    const { t } = useTranslation();

    useEffect(function () {
        if (openPopup) {
            updatePageData();
        }
    }, [openPopup]);

    return (
        <GlobitsPopup
            scroll={"body"}
            size="md"
            open={openPopup}
            noDialogContent
            title={"Chọn đơn vị áp dụng"}
            onClosePopup={handleClosePopup}
            popupId={"chooseDpmPopup"}
        >
            <div className="dialog-body"
            >
                <DialogContent className="p-12">

                    <Grid container spacing={2}>
                        <Grid item xs={4}></Grid>
                        <Grid item xs={8}>
                            <GlobitsSearchInput search={updatePageData} />
                        </Grid>

                        <Grid item xs={12}>
                            <ChooseListDepartmentInSalaryConfig />
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
                            className="btn btn-gray d-inline-flex"
                            // color="secondary"
                            type="button"
                            onClick={handleClosePopup}
                        >
                            Đóng
                        </Button>
                    </div>
                </DialogActions>
            </div>

        </GlobitsPopup>
    );
}

export default memo(observer(ChooseSalaryConfigDepartmentPopup));