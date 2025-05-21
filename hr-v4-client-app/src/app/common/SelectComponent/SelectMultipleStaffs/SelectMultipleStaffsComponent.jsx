import React, { memo, useEffect, useState } from "react";
import { useField, useFormikContext } from "formik";
import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import DeleteIcon from "@material-ui/icons/Delete";
import { useTranslation } from "react-i18next";
import TouchAppIcon from "@material-ui/icons/TouchApp";
import ChosenStaffList from "./ChosenStaffList";
import ChooseMultipleStaffsPopup from "./ChooseMultipleStaffsPopup";
import { useStore } from "../../../stores";

/*
 *   Select multiple staffs
 */
function SelectMultipleStaffsComponent(props) {
    const { t } = useTranslation();
    const { userStore } = useStore();
    const { usingStaffSO } = userStore;

    const [openChooseStaff, setOpenChooseStaff] = useState(false);

    const [listSelectedStaffs, setListSelectedStaffs] = useState([]);
    const [field, meta, helpers] = useField(props.name);
    const { required } = props;
    const { values, setFieldValue } = useFormikContext();

    function handleCloseChooseMultipleStaffs(){
        const syncFormWithStore = async () => {
            await setFieldValue("organization", usingStaffSO?.organization || null);
            await setFieldValue("department", usingStaffSO?.department || null);
            await setFieldValue("positionTitle", usingStaffSO?.positionTitle || null);
        };

        if (openChooseStaff)
            syncFormWithStore();
        
        setOpenChooseStaff(false);
    }

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <Grid container alignItems='center' justifyContent='space-between'>
                    {/* Label hiển thị bên trái */}
                    <Grid item>
                        <p className='m-0' style={{ fontSize: "14px" }}>
                            <strong>{`Danh sách nhân viên `}</strong>
                            {required ? <span style={{ color: "red" }}> * </span> : <></>}
                            {`(${values?.staffs?.length || "0"} nhân viên được chọn)`}
                        </p>
                        {meta.touched && meta.error && (
                            <p style={{ color: "red", fontSize: "14px", marginTop: "8px" }}>{meta.error}</p>
                        )}
                    </Grid>
                    {/* ButtonGroup hiển thị bên phải */}
                    <Grid item>
                        <ButtonGroup color='container' aria-label='outlined primary button group'>
                            <Tooltip arrow placement='top' title={"Xóa nhân viên đã chọn"}>
                                <Button
                                    startIcon={<DeleteIcon />}
                                    type='button'
                                    onClick={() => {
                                        const updatedStaffs = values?.staffs?.filter(
                                            (staff) => !listSelectedStaffs.some((selected) => selected.id === staff.id)
                                        );
                                        setFieldValue("staffs", updatedStaffs); // Cập nhật danh sách staffs
                                        setListSelectedStaffs([]); // Reset danh sách nhân viên đã chọn
                                    }}
                                    disabled={listSelectedStaffs?.length === 0}>
                                    Xóa
                                </Button>
                            </Tooltip>
                            <Tooltip arrow placement='top' title={"Chọn nhiều nhân viên"}>
                                <Button
                                    startIcon={<TouchAppIcon />}
                                    type='button'
                                    onClick={() => setOpenChooseStaff(true)}>
                                    Chọn nhân viên
                                </Button>
                            </Tooltip>
                        </ButtonGroup>
                    </Grid>
                </Grid>
            </Grid>

            <Grid item xs={12} style={{ overflowX: "auto" }}>
                <ChosenStaffList
                    listSelectedStaffs={listSelectedStaffs} // Truyền danh sách nhân viên được chọn
                    setListSelectedStaffs={setListSelectedStaffs} // Truyền hàm cập nhật danh sách
                />
            </Grid>

            {openChooseStaff && (
                <ChooseMultipleStaffsPopup
                    open={openChooseStaff}
                    isDisableFilter={props?.isDisableFilter}
                    isResetStore={props?.isResetStore}
                    handleClose={handleCloseChooseMultipleStaffs}
                    searchObject={props?.searchObject}
                />
            )}
        </Grid>
    );
}

export default memo(observer(SelectMultipleStaffsComponent));
