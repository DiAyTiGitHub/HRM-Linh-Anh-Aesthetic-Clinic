import React , { memo , useEffect , useMemo , useState } from "react";
import { Button , ButtonGroup , Grid , Icon , IconButton , makeStyles , Tooltip } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import { useTranslation } from "react-i18next";
import { FieldArray , useFormikContext } from "formik";
import { useParams } from "react-router";
import { observer } from "mobx-react";
import GlobitsTable from "app/common/GlobitsTable";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import localStorageService from "app/services/localStorageService";
import { useStore } from "app/stores";
import "react-toastify/dist/ReactToastify.css";
import StaffSalaryTemplateCUForm from "app/views/StaffSalaryTemplate/StaffSalaryTemplateCUForm";
import SalaryValueHistoriesPopup from "app/views/StaffSalaryTemplate/SalaryValueHistories/SalaryValueHistoriesPopup";
import StaffSalaryTemplateView from "../../StaffSalaryTemplate/StaffSalaryTemplateView";

const useStyles = makeStyles((theme) => ({
    root:{
        "& .MuiAccordion-rounded, & .MuiPaper-root":{borderRadius:"5px"} ,
        "& .MuiAccordionSummary-root":{
            borderRadius:"5px" ,
            color:"#5899d1" ,
            fontWeight:"400" ,
            "& .MuiTypography-root":{fontSize:"1rem"} ,
        } ,
        "& .Mui-expanded .MuiAccordionSummary-root":{
            backgroundColor:"#EBF3F9" ,
            color:"#5899d1" ,
            fontWeight:"700" ,
            maxHeight:"50px !important" ,
            minHeight:"50px !important" ,
        } ,
        "& .MuiTypography-root":{fontWeight:700} ,
        "& .MuiButton-root":{borderRadius:"0.125rem !important"} ,
    } ,
    buttonGroupSpacing:{marginBottom:"10px"} ,
}));

function StaffSalaryTemplate() {
    const {t} = useTranslation();
    const classes = useStyles();
    const {values} = useFormikContext();

    const {
        staffSalaryTemplateStore ,
        staffSalaryItemValueStore

    } = useStore();

    const {
        openValueHitoriesPopup
    } = staffSalaryItemValueStore;

    const {id} = useParams();
    const [isLoading , setIsLoading] = useState(false);

    const {
        pagingStaffSalaryTemplate ,
        staffSalaryTemplateList ,
        handleOpenCreateEdit ,
        handleDelete ,
        openCreateEditPopup ,
        openViewPopup ,
        setOpenCreateEditPopup ,
        handleConfirmDelete ,
        handleClose ,
        openConfirmDeletePopup ,
        setSelectedStaffSalaryTemplate ,
        searchObject ,
        handleOpenView
    } = staffSalaryTemplateStore;

    useEffect(() => {
        staffSalaryTemplateStore.openCreateEditPopup = false;
        if (id) {
            setIsLoading(true);
            searchObject.staffId = id;
            searchObject.staff = {id:id};
            pagingStaffSalaryTemplate().finally(() => {
                setIsLoading(false);
            });
        }
    } , [id]);

    const isAdmin = useMemo(() => {
        const roles = localStorageService.getLoginUser()?.user?.roles?.map((r) => r.authority) || [];
        return ["HR_MANAGER" , "ROLE_ADMIN" , "ROLE_SUPER_ADMIN"].some((role) => roles.includes(role));
    } , []);

    const handleOpenForm = () => {
        setOpenCreateEditPopup(true);
        setSelectedStaffSalaryTemplate({staff:{id}});
    };

    const columns = [
        {
            title:t("general.action") ,
            width:"5%" ,
            align:"center" ,
            render:(rowData) => {
                if (isAdmin) {
                    return (
                        <div className='flex flex-middle justify-center'>
                            <Tooltip
                                arrow
                                placement="top"
                                title={"Chi tiết thành phần lương"}
                            >
                                <IconButton
                                    className="ml-4"
                                    size="small"
                                    onClick={() => handleOpenView(rowData?.id)}
                                >
                                    <Icon fontSize="small" style={{color:"green"}}>
                                        remove_red_eye
                                    </Icon>
                                </IconButton>

                            </Tooltip>
                            <Tooltip title='Cập nhật thông tin' placement='top'>
                                <IconButton size='small' onClick={() => handleOpenCreateEdit(rowData?.id)}>
                                    <Icon fontSize='small' color='primary'>
                                        edit
                                    </Icon>
                                </IconButton>
                            </Tooltip>
                            <Tooltip title='Xóa' placement='top'>
                                <IconButton size='small' className='ml-4' onClick={() => handleDelete(rowData)}>
                                    <Icon fontSize='small' color='secondary'>
                                        delete
                                    </Icon>
                                </IconButton>
                            </Tooltip>
                        </div>
                    );
                }
            } ,
        } ,
        {title:t("Mẫu bảng lương") , field:"salaryTemplate.name" , align:"center"} ,
        // {
        //     title: t("Thời gian bắt đầu"),
        //     field: "fromDate",
        //     align: "left",
        //     render: (data) => data?.fromDate && <span>{formatDate("DD/MM/YYYY", data?.fromDate)}</span>,
        // },
        // {
        //     title: t("Thời gian kết thúc"),
        //     field: "toDate",
        //     align: "left",
        //     render: (data) => data?.toDate && <span>{formatDate("DD/MM/YYYY", data?.toDate)}</span>,
        // },
    ];

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <FieldArray
                    name='agreements'
                    render={() => (
                        <>
                            {isAdmin && values?.id && (
                                <ButtonGroup className={classes.buttonGroupSpacing}>
                                    <Button startIcon={<AddIcon/>} onClick={handleOpenForm}>
                                        Thêm mới
                                    </Button>
                                </ButtonGroup>
                            )}
                            <Grid container spacing={2} className={classes.root}>
                                <Grid item xs={12}>
                                    {isLoading ? (
                                        <p className='w-100 text-center'>Đang tải dữ liệu...</p>
                                    ) : staffSalaryTemplateList?.length ? (
                                        <GlobitsTable
                                            data={staffSalaryTemplateList}
                                            columns={columns}
                                            maxWidth='100%'
                                            nonePagination
                                            selection={false}
                                        />
                                    ) : (
                                        <p className='w-100 text-center'>Chưa có mẫu bảng lương</p>
                                    )}
                                </Grid>
                            </Grid>
                        </>
                    )}
                />

                {openCreateEditPopup && <StaffSalaryTemplateCUForm hasStaff={true}/>}

                {openViewPopup && <StaffSalaryTemplateView hasStaff={true} readOnly/>}


                {openValueHitoriesPopup && (
                    <SalaryValueHistoriesPopup/>
                )}

                {openConfirmDeletePopup && (
                    <GlobitsConfirmationDialog
                        open={openConfirmDeletePopup}
                        onConfirmDialogClose={() => handleClose()}
                        onYesClick={handleConfirmDelete}
                        title={t("confirm_dialog.delete.title")}
                        text={t("confirm_dialog.delete.text")}
                        agree={t("confirm_dialog.delete.agree")}
                        cancel={t("confirm_dialog.delete.cancel")}
                    />
                )}
            </Grid>
        </Grid>
    );
}

export default memo(observer(StaffSalaryTemplate));
