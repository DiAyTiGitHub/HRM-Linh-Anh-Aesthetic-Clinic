import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import { TouchApp } from "@material-ui/icons";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import FormikFocusError from "app/common/FormikFocusError";
import { Form, Formik, useFormikContext } from "formik";
import React from "react";
import * as Yup from "yup";
import { DepartmentNodeType } from "../Nodes/DepartmentNode";
import { OrganizationNodeType } from "../Nodes/OrganizationNode";
import { PositionNodeType } from "../Nodes/PositionNode";
import { StaffNodeType } from "../Nodes/StaffNode";
import SelectDepartmentPopup from "./SelectDepartmentPopup";
import SelectOrganizationPopup from "./SelectOrganizationPopup";
import SelectPositionPopup from "./SelectPositionPopup";
import SelectStaffPopup from "./SelectStaffPopup";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";

export default function EditNodePopup({ open, handleClose, data, node, handleFormSubmit }) {
    const validationSchema = Yup.object({});

    return (
        <GlobitsPopupV2
            popupId='edit-node-popup'
            scroll={"body"}
            size='sm'
            open={open}
            noDialogContent
            title={"Cập nhật thông tin"}
            onClosePopup={handleClose}>
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={data}
                onSubmit={(values) => handleFormSubmit(values)}>
                {({ isSubmitting, values, setFieldValue, initialValues }) => {
                    return (
                        <Form autoComplete='off' autocomplete='off'>
                            <div className='dialog-body'>
                                <DialogContent className='p-12'>
                                    <FormikFocusError />
                                    <Grid container spacing={2}>
                                        <FieldByType type={node?.type} />
                                    </Grid>
                                </DialogContent>
                            </div>

                            <div className='dialog-footer py-8'>
                                <DialogActions className='p-0'>
                                    <div className='flex flex-space-between flex-middle'>
                                        <Button
                                            startIcon={<BlockIcon />}
                                            className='btn btn-secondary d-inline-flex mr-8'
                                            onClick={() => handleClose()}>
                                            Huỷ
                                        </Button>
                                        <Button
                                            startIcon={<SaveIcon />}
                                            className='btn btn-primary d-inline-flex'
                                            type='submit'
                                            disabled={isSubmitting}>
                                            Cập nhật
                                        </Button>
                                    </div>
                                </DialogActions>
                            </div>
                        </Form>
                    );
                }}
            </Formik>
        </GlobitsPopupV2>
    );
}

// hiển thị trường theo loại node
const FieldByType = ({ type }) => {
    const { setFieldValue } = useFormikContext();
    const [openSelect, setOpenSelect] = React.useState(null);
    console.log(type);
    if (type === StaffNodeType.type) {
        return (
            <>
                <Grid item xs={12}>
                    {openSelect?.open && <SelectStaffPopup {...openSelect} />}
                    <GlobitsTextField
                        label='Nhân viên'
                        name='staff.displayName'
                        disabled
                        InputProps={{
                            endAdornment: (
                                <Button
                                    variant='contained'
                                    className='bgc-lighter-dark-green px-0'
                                    onClick={() => {
                                        setOpenSelect({
                                            open: true,
                                            handleSelect: (value) => {
                                                setFieldValue("staff", value);
                                                setFieldValue("name", value?.displayName);
                                                setFieldValue("code", value?.staffCode);
                                                setFieldValue("title", value?.jobTitle);
                                                setFieldValue("objectId", value?.id);
                                                setOpenSelect(null);
                                            },
                                            handleClose: () => setOpenSelect(null),
                                        });
                                    }}>
                                    <TouchApp className='text-white' />
                                </Button>
                            ),
                        }}
                    />
                </Grid>
                <Grid item xs={12}>
                    <GlobitsTextField label='Tên hiển thị' name='name' />
                </Grid>
                <Grid item xs={12}>
                    <GlobitsTextField label='Mã' name='code' />
                </Grid>
                <Grid item xs={12}>
                    <GlobitsTextField label='Vị trí' name='title' />
                </Grid>
                {/* <Grid item xs={12}>
          <GlobitsTextField label="STT" name="stepIndex" />
        </Grid> */}
                {/* <Grid item xs={12}>
          <GlobitsPagingAutocomplete
            label="Người, bộ phận thực hiện"
            name="participant"
            api={pagingParticipant}
          />
        </Grid>
        <Grid item xs={12}>
          <GlobitsSelectInput
            label="Loại"
            name="actionType"
            options={StaffNodeType.ListActionType}
          />
        </Grid> */}
            </>
        );
    } else if (type === DepartmentNodeType.type) {
        return (
            <>
                <Grid item xs={12}>
                    {openSelect?.open && <SelectDepartmentPopup {...openSelect} />}
                    <GlobitsTextField
                        label='Phòng ban'
                        name='department.name'
                        disabled
                        InputProps={{
                            endAdornment: (
                                <Button
                                    variant='contained'
                                    className='bgc-lighter-dark-green px-0'
                                    onClick={() => {
                                        setOpenSelect({
                                            open: true,
                                            handleSelect: (value) => {
                                                setFieldValue("department", value);
                                                setFieldValue("name", value?.name);
                                                setFieldValue("code", value?.code);
                                                setFieldValue("objectId", value?.id);
                                                setOpenSelect(null);
                                            },
                                            handleClose: () => setOpenSelect(null),
                                        });
                                    }}>
                                    <TouchApp className='text-white' />
                                </Button>
                            ),
                        }}
                    />
                </Grid>
                <Grid item xs={12}>
                    <GlobitsTextField label='Tên hiển thị' name='name' />
                </Grid>
                <Grid item xs={12}>
                    <GlobitsTextField label='Mã' name='code' />
                </Grid>
            </>
        );
    } else if (type === OrganizationNodeType.type) {
        return (
            <>
                <Grid item xs={12}>
                    {openSelect?.open && <SelectOrganizationPopup {...openSelect} />}
                    <GlobitsTextField
                        label='Đơn vị'
                        name='organization.name'
                        disabled
                        InputProps={{
                            endAdornment: (
                                <Button
                                    variant='contained'
                                    className='bgc-lighter-dark-green px-0'
                                    onClick={() => {
                                        setOpenSelect({
                                            open: true,
                                            handleSelect: (value) => {
                                                setFieldValue("organization", value);
                                                setFieldValue("name", value?.name);
                                                setFieldValue("code", value?.code);
                                                setFieldValue("objectId", value?.id);
                                                setOpenSelect(null);
                                            },
                                            handleClose: () => setOpenSelect(null),
                                        });
                                    }}>
                                    <TouchApp className='text-white' />
                                </Button>
                            ),
                        }}
                    />
                </Grid>
                <Grid item xs={12}>
                    <GlobitsTextField label='Tên hiển thị' name='name' />
                </Grid>
                <Grid item xs={12}>
                    <GlobitsTextField label='Mã' name='code' />
                </Grid>
            </>
        );
    } else if (type === PositionNodeType.type) {
        return (
            <>
                <Grid item xs={12}>
                    {openSelect?.open && <SelectPositionPopup {...openSelect} />}
                    <GlobitsTextField
                        label='Vị trí'
                        name='position.name'
                        disabled
                        InputProps={{
                            endAdornment: (
                                <Button
                                    variant='contained'
                                    className='bgc-lighter-dark-green px-0'
                                    onClick={() => {
                                        setOpenSelect({
                                            open: true,
                                            handleSelect: (value) => {
                                                setFieldValue("position", value);
                                                setFieldValue("name", value?.staff?.displayName || "Vacant");
                                                setFieldValue("title", value?.name);
                                                setFieldValue("code", value?.code);
                                                setFieldValue("objectId", value?.id);
                                                setOpenSelect(null);
                                            },
                                            handleClose: () => setOpenSelect(null),
                                        });
                                    }}>
                                    <TouchApp className='text-white' />
                                </Button>
                            ),
                        }}
                    />
                </Grid>
                <Grid item xs={12}>
                    <GlobitsTextField label='Vị trí' name='title' />
                </Grid>
                <Grid item xs={12}>
                    <GlobitsTextField label='Nhân viên' name='name' />
                </Grid>
                <Grid item xs={12}>
                    <GlobitsTextField label='Mã' name='code' />
                </Grid>
            </>
        );
    }
    return null;
};
