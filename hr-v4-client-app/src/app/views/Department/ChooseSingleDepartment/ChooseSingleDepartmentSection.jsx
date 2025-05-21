import React, { memo, useEffect, useState } from "react";
import { observer } from "mobx-react";
import {
    Button,
    DialogContent,
    Grid,
    Tooltip,
} from "@material-ui/core";
import Paper from "@material-ui/core/Paper";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import UsingStaffPopup from "./ChooseSingleDepartmentPopup";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import TouchAppIcon from '@material-ui/icons/TouchApp';
import { useFormikContext } from "formik";
import ChooseSingleDepartmentPopup from "./ChooseSingleDepartmentPopup";
import { pagingAllDepartments } from "../DepartmentService";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import SelectDepartmentList from "../SelectParent/SelectDepartmentList";
import GlobitsSearchInput from "app/common/GlobitsSearchInput";
import ChooseSingleDepartmentList from "./ChooseSingleDepartmentList";

function ChooseSingleDepartmentSection(props) {
    const { t } = useTranslation();
    const {
        label,
        required = false,
        disabled = false,
        name = "department"
    } = props;

    const { departmentStore } = useStore();

    const { handleToggleDepartmentPopup, updatePageData } = departmentStore;


    const [openPopup, setOpenPopup] = useState(false);

    function handleClosePopup() {
        setOpenPopup(false);
    }

    const { values } = useFormikContext();

    return (
        <>
            <Grid container spacing={1}>
                <Grid item xs={9}>
                    <GlobitsTextField
                        required={required}
                        placeholder="Chưa chọn phòng ban"
                        label={label || "Phòng ban"}
                        name={name}
                        disabled={disabled}
                        value={values[name]?.name ? values[name]?.name : "Chưa chọn phòng ban"}
                    />

                    {/* <GlobitsPagingAutocompleteV2
                        api={pagingAllDepartments}
                        label={label || "Phòng ban"}
                        name={name}
                        placeholder="Chưa chọn phòng ban"
                        required={required}
                        disabled={disabled}
                    /> */}
                </Grid>

                <Grid item xs={3} className="flex align-end">
                    <Tooltip placement="top" title="Chọn phòng ban">
                        <Button
                            fullWidth
                            variant="contained"
                            className="btn bgc-lighter-dark-blue text-white d-inline-flex my-2"
                            onClick={() => setOpenPopup(true)}
                            disabled={disabled}
                        >
                            <TouchAppIcon className="text-white" />
                        </Button>
                    </Tooltip>
                </Grid>
            </Grid>



            {/* {openPopup && (
                <ChooseSingleDepartmentPopup
                    open={openPopup}
                    handleClose={handleClosePopup}
                    disabled={disabled}
                    name={name}
                />
            )} */}


            {openPopup && (
                <GlobitsPopupV2
                    size="md"
                    scroll={"body"}
                    open={openPopup}
                    noDialogContent
                    onClosePopup={handleClosePopup}
                    popupId={"chooseSingleDepartmentPopup"}
                    title='Danh sách phòng ban'
                >
                    <DialogContent
                        className="p-12"
                        style={{ overflowY: "auto", maxHeight: "88vh" }}
                    >
                        <Grid container spacing={2}>
                            <Grid item lg={6} md={6} sm={4} xs={4}></Grid>
                            <Grid item lg={6} md={6} sm={8} xs={8}>
                                <GlobitsSearchInput search={updatePageData} />
                            </Grid>

                            <Grid item xs={12}>
                                <ChooseSingleDepartmentList
                                    name={name}
                                    handleClose={handleClosePopup}
                                />
                            </Grid>
                        </Grid>

                    </DialogContent>

                </GlobitsPopupV2>
            )}
        </>
    );
}

export default memo(observer(ChooseSingleDepartmentSection));
