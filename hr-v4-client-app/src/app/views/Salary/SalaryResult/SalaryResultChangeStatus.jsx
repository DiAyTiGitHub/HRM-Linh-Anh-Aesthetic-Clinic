import { Button , Grid , Icon , IconButton , Tooltip } from "@material-ui/core";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import ConstantList from "app/appConfig";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import React , { memo , useRef , useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import SalarySelectPrintRows from "./SalarySelectPrintRows";
import GlobitsColorfulThemePopup from "../../../common/GlobitsColorfulThemePopup";
import { Form , Formik } from "formik";
import FormikFocusError from "../../../common/FormikFocusError";
import SaveIcon from "@material-ui/icons/Save";
import DeleteIcon from "@material-ui/icons/Delete";

function SalaryResultChangeStatus() {
    const {salaryResultStore} = useStore();
    const {t} = useTranslation();
    const history = useHistory();
    const {
        listOnDelete ,
        handleClose ,
        getApprovalStatusName ,
        handleConfirmChangeStatus ,
        handleSelectListDelete ,
        openConfirmChangeStatus ,
        pagingSalaryResult
    } = salaryResultStore;

    const columns = [
        {
            title:t("general.action") ,
            minWidth:"48px" ,
            render:(rowData) => {
                return (
                    <div className='flex justify-center'>
                        <Tooltip title='Xóa bảng lương' placement='top'>
                            <IconButton size='small' onClick={() => {
                                let list = listOnDelete.filter(item => !item.isSelected);
                                // Gọi hàm xử lý với danh sách mới
                                handleSelectListDelete(list);
                            }}>
                                <Icon fontSize='small' color='secondary'>
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    </div>
                );
            } ,
        } ,
        {
            title:"Mã" ,
            field:"code" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,
        {
            title:"Tên bảng lương" ,
            field:"name" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,
        {
            title:"Kỳ lương" ,
            field:"salaryPeriod.name" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,
        {
            title:"Mẫu áp dụng" ,
            field:"salaryTemplate.name" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,
        {
            title:"Mô tả" ,
            field:"description" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,
    ];

    return (
        <GlobitsColorfulThemePopup
            open={openConfirmChangeStatus}
            handleClose={handleClose}
            hideFooter
            size="lg"
            onConfirm={handleConfirmChangeStatus}
        >
            <div className="dialog-body">
                <Grid container spacing={2}>
                    <Grid item xs={12} sm={8} md={9}>
                        <div className="dialogScrollContent">
                            <h6 className="text-red">
                                <strong className='pt-4 flex'>
                                    Danh sách chọn cập nhật thành {
                                    getApprovalStatusName()
                                }
                                </strong>
                            </h6>

                            <GlobitsTable
                                data={listOnDelete}
                                columns={columns}
                                nonePagination
                            />
                        </div>
                    </Grid>

                    <Grid item xs={12} sm={4} md={3}>
                        <Formik
                            enableReinitialize
                            initialValues={{}}
                            onSubmit={handleConfirmChangeStatus}
                        >
                            {({isSubmitting , values , setFieldValue , initialValues}) => {

                                return (
                                    <Form autoComplete="off" autocomplete="off">
                                        <FormikFocusError/>

                                        <div className="pt-12" style={{color:"#5e6c84"}}>
                                            {t("task.action")}
                                        </div>

                                        <div className="listButton">
                                            <Button
                                                variant="contained"
                                                className="btn-green"
                                                startIcon={<SaveIcon/>}
                                                type="submit"
                                                disabled={isSubmitting}
                                            >
                                                Xác nhận
                                            </Button>

                                            <Button
                                                startIcon={<DeleteIcon/>}
                                                variant="contained"
                                                onClick={() => {
                                                    handleClose();
                                                    pagingSalaryResult()
                                                }}
                                                className="btn-danger"
                                                disabled={isSubmitting}
                                            >
                                                Hủy bỏ
                                            </Button>
                                        </div>
                                    </Form>
                                );
                            }}
                        </Formik>
                    </Grid>
                </Grid>
            </div>
        </GlobitsColorfulThemePopup>
    );
}

export default memo(observer(SalaryResultChangeStatus));
