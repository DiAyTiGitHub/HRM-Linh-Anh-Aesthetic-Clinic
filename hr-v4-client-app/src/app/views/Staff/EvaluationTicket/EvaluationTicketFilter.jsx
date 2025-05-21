import {observer} from "mobx-react";
import {Form, Formik} from "formik";
import {Button, ButtonGroup, Collapse, DialogActions, DialogContent, Grid, Tooltip,} from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import {useStore} from "../../../stores";
import {useTranslation} from "react-i18next";
import ConstantList from "../../../appConfig";
import {useHistory} from "react-router-dom";
import React from "react";
import {pagingStaff} from "../../HumanResourcesInformation/StaffService";
import GlobitsTextField from "../../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";
import FilterListIcon from "@material-ui/icons/FilterList";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import GlobitsSelectInput from "../../../common/form/GlobitsSelectInput";
import {EVALUATION_STATUS} from "../../../LocalConstants";
import {TransferWithinAStation} from "@material-ui/icons";
import {pagingAllDepartments} from "../../Department/DepartmentService";
import {pagingPositionTitle} from "../../PositionTitle/PositionTitleService";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import GlobitsPopupV2 from "../../../common/GlobitsPopupV2";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import {pagingAllOrg} from "../../Organization/OrganizationService";
import {paging} from "../../System/SystemParam/Evaluation/EvaluationTemplate/EvaluationTemplateService";
import GlobitsPagingAutocomplete from "../../../common/form/GlobitsPagingAutocomplete";
import * as Yup from "yup";

const EvaluationTicketFilter = () => {
    const {t} = useTranslation();
    const {
        isOpenSaveForm,
        intactSearchObject,
        search,
        handleSetSearchObject,
        handleIsOpenSaveForm,
        handleTogglePopupFilter,
        isOpenFilter,
        pageEvaluationForms,
        selected,
        transferEvaluationForm,
        exportContractApprovalList
    } = useStore().evaluationTicketStore

    const history = useHistory();

    const handleNavigateToSaveFrom = (id, templateId) => {
        if (id && templateId) {
            history.push(ConstantList.ROOT_PATH + `staff-evaluation-ticket/save/ ${id}/${templateId}`);
        } else {
            history.push(ConstantList.ROOT_PATH + "staff-evaluation-ticket/save/" + id);
        }
    };
    const handleSubmitSearch = async (values) => {
        pageEvaluationForms(values)
    }


    const resetFilterForm = (resetForm) => {
        resetForm()
        handleSetSearchObject(intactSearchObject);
    }

    const validationSchema = Yup.object({
        staff: Yup.object().nullable().required(t("validation.required"))
    })

    return (
        <>
            <Formik
                enableReinitialize
                initialValues={search}
                onSubmit={handleSubmitSearch}>
                {({resetForm, values, setFieldValue, setValues}) => {
                    return (
                        <Form autoComplete='off'>
                            <Grid container spacing={2}>
                                <Grid item xs={12} xl={6} md={6}>
                                    <ButtonGroup color='container' aria-label='outlined primary button group'>
                                        <Tooltip placement='top' title='Thêm mới phiếu đánh giá'>
                                            <Button
                                                startIcon={<AddIcon fontSize='small'/>}
                                                onClick={() => {
                                                    handleIsOpenSaveForm(true)
                                                }}>
                                                {t("general.button.add")}
                                            </Button>
                                        </Tooltip>
                                        <Tooltip placement='top' title='Chuyển tiếp'>
                                            <Button
                                                disabled={selected?.length !== 1}
                                                startIcon={<TransferWithinAStation fontSize='small'/>}
                                                onClick={() => {
                                                    transferEvaluationForm(selected[0]?.id)
                                                }}>
                                                Chuyển tiếp
                                            </Button>
                                        </Tooltip>
                                        <Tooltip placement='top' title='Chuyển tiếp'>
                                            <Button
                                                onClick={() => {
                                                    exportContractApprovalList(values)
                                                }}>
                                                Xuất Excel
                                            </Button>
                                        </Tooltip>
                                    </ButtonGroup>
                                </Grid>
                                <Grid item xs={12} xl={6} md={6}>
                                    <div className='flex justify-between align-center'>
                                        <Tooltip placement='top'
                                                 title='Tìm kiếm theo tên, mã nhân viên...'>
                                            <GlobitsTextField
                                                placeholder='Tìm kiếm theo tên, mã nhân viên...'
                                                name='keyword'
                                                variant='outlined'
                                                notDelay
                                            />
                                        </Tooltip>

                                        <ButtonGroup
                                            className='filterButtonV4'
                                            color='container'
                                            aria-label='outlined primary button group'>
                                            <Button
                                                startIcon={<SearchIcon className={``}/>}
                                                className='ml-8 d-inline-flex py-2 px-8 btnHrStyle'
                                                type='submit'>
                                                Tìm kiếm
                                            </Button>
                                            <Button
                                                startIcon={
                                                    <FilterListIcon
                                                        className={` filterRotateIcon ${isOpenFilter && "onRotate"}`}
                                                    />
                                                }
                                                className=' d-inline-flex py-2 px-8 btnHrStyle'
                                                onClick={() => handleTogglePopupFilter()}
                                            >
                                                Bộ lọc
                                            </Button>
                                        </ButtonGroup>
                                    </div>
                                </Grid>
                                <Grid item xs={12}>
                                    <Collapse in={isOpenFilter} className="filterPopup">
                                        <div className="flex flex-column">
                                            <Grid container spacing={2}>
                                                <Grid item xs={12}>
                                                    <div className="filterContent pt-8">
                                                        <Grid container spacing={2}>
                                                            <Grid item xs={12}>
                                                                <Grid container spacing={2}
                                                                      className={"flex flex-end"}
                                                                >
                                                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                                                        <GlobitsPagingAutocomplete
                                                                            label={"Phòng ban"}
                                                                            name="department"
                                                                            api={pagingAllDepartments}
                                                                            searchObject={{
                                                                                pageIndex: 1,
                                                                                pageSize: 9999,
                                                                                keyword: "",
                                                                                organizationId: values?.organization?.id,
                                                                            }}
                                                                            getOptionLabel={(option) =>
                                                                                [option?.name, option?.code].filter(Boolean).join(' - ') || ''
                                                                            }
                                                                        />
                                                                    </Grid>
                                                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                                                        <GlobitsPagingAutocomplete
                                                                            name="positionTitle"
                                                                            label="Chức danh"
                                                                            api={pagingPositionTitle}
                                                                            searchObject={{
                                                                                departmentId: values?.department?.id,
                                                                            }}
                                                                        />
                                                                    </Grid>
                                                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                                                        <GlobitsPagingAutocomplete
                                                                            label={"Nhân viên"}
                                                                            name={"staff"}
                                                                            api={pagingStaff}
                                                                            searchObject={{
                                                                                organizationId: values?.organization?.id,
                                                                                departmentId: values?.department?.id,
                                                                                positionTitleId: values?.positionTitle?.id
                                                                            }}
                                                                            handleChange={(_, value) => {
                                                                                setFieldValue("staff", value);
                                                                                setFieldValue("staffId", value?.id);
                                                                            }}
                                                                            getOptionLabel={(option) => {
                                                                                if (!option) return "";

                                                                                const name = option.displayName || "";
                                                                                const code = option.staffCode ? ` - ${option.staffCode}` : "";
                                                                                const position = option.currentPosition?.name
                                                                                    ? ` (${option.currentPosition.name})`
                                                                                    : "";

                                                                                return `${name}${code}${position}`;
                                                                            }}
                                                                        />
                                                                    </Grid>
                                                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                                                        <GlobitsPagingAutocomplete
                                                                            label={"Quản lý trực tiếp"}
                                                                            name={"directManager"}
                                                                            api={pagingStaff}
                                                                            handleChange={(_, value) => {
                                                                                setFieldValue("directManager", value);
                                                                                setFieldValue("directManagerId", value?.id);
                                                                            }}
                                                                            getOptionLabel={(option) => {
                                                                                if (!option) return "";

                                                                                const name = option.displayName || "";
                                                                                const code = option.staffCode ? ` - ${option.staffCode}` : "";
                                                                                const position = option.currentPosition?.name
                                                                                    ? ` (${option.currentPosition.name})`
                                                                                    : "";

                                                                                return `${name}${code}${position}`;
                                                                            }}
                                                                        />
                                                                    </Grid>
                                                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                                                        <GlobitsSelectInput
                                                                            label={"Trạng thái"}
                                                                            displayData={"name"}
                                                                            name={"status"}
                                                                            keyValue="id"
                                                                            options={[
                                                                                {
                                                                                    id: EVALUATION_STATUS.PASS,
                                                                                    name: "Đạt"
                                                                                },
                                                                                {
                                                                                    id: EVALUATION_STATUS.FAIL,
                                                                                    name: "Không đạt"
                                                                                },
                                                                            ]}
                                                                        />
                                                                    </Grid>
                                                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                                                        <GlobitsSelectInput
                                                                            label={"Loại HĐLĐ"}
                                                                            displayData={"name"}
                                                                            name={"contractType"}
                                                                            keyValue="id"
                                                                            options={[
                                                                                {
                                                                                    id: "XĐTH",
                                                                                    name: "Xác định thời hạn"
                                                                                },
                                                                                {
                                                                                    id: "KXĐTH",
                                                                                    name: "Không xác định thời hạn"
                                                                                },
                                                                            ]}
                                                                        />
                                                                    </Grid>
                                                                </Grid>
                                                            </Grid>
                                                        </Grid>
                                                        <div className="py-8 mt-12 border-bottom-fade border-top-fade">
                                                            <div className="flex justify-end">
                                                                <ButtonGroup
                                                                    color="container"
                                                                    aria-label="outlined primary button group"
                                                                >
                                                                    <Button
                                                                        type="button"
                                                                        startIcon={<RotateLeftIcon/>}
                                                                        onClick={() => resetFilterForm(resetForm)}
                                                                    >
                                                                        Đặt lại
                                                                    </Button>
                                                                    <Button
                                                                        type="button"
                                                                        startIcon={<HighlightOffIcon/>}
                                                                        onClick={handleTogglePopupFilter}
                                                                    >
                                                                        Đóng bộ lọc
                                                                    </Button>
                                                                </ButtonGroup>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </Grid>
                                            </Grid>
                                        </div>
                                    </Collapse>
                                </Grid>
                            </Grid>
                        </Form>)
                }}
            </Formik>
            <Formik enableReinitialize
                    validationSchema={validationSchema}
                    initialValues={{
                        ...(search?.staff && search?.staffName ? {
                            id: search.staff,
                            displayName: search.staffName
                        } : {}),
                    }}
                    onSubmit={(value) => {
                        handleNavigateToSaveFrom(value?.staff?.id, value?.evaluationTemplate?.id)
                    }}
            >
                {({resetForm, values, setFieldValue, setValues}) => {
                    return (
                        <GlobitsPopupV2
                            open={isOpenSaveForm}
                            scroll={"body"}
                            title='Danh sách nhân viên'
                            size="sm"
                            onClosePopup={() => handleIsOpenSaveForm(false)}
                        >
                            <Form autoComplete='off'>
                                <DialogContent className="o-hidden p-12">
                                    <Grid container spacing={2} className="flex">
                                        <Grid item xs={12}>
                                            <GlobitsPagingAutocomplete
                                                name="organization"
                                                label="Đơn vị"
                                                api={pagingAllOrg}
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <GlobitsPagingAutocomplete
                                                label={"Phòng ban"}
                                                name="department"
                                                api={pagingAllDepartments}
                                                searchObject={{
                                                    pageIndex: 1, pageSize: 9999, keyword: "",
                                                    organizationId: values?.organization?.id,
                                                }}
                                                getOptionLabel={(option) =>
                                                    [option?.name, option?.code].filter(Boolean).join(' - ') || ''
                                                }
                                            />
                                        </Grid>

                                        <Grid item xs={12}>
                                            <GlobitsPagingAutocomplete
                                                name="positionTitle"
                                                label="Chức danh"
                                                api={pagingPositionTitle}
                                                searchObject={{
                                                    departmentId: values?.department?.id,
                                                }}
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <GlobitsPagingAutocomplete
                                                label="Nhân viên"
                                                required
                                                requiredLabel
                                                displayData={"displayName"}
                                                name={"staff"}
                                                api={pagingStaff}
                                                searchObject={{
                                                    organizationId: values?.organization?.id,
                                                    departmentId: values?.department?.id,
                                                    positionTitleId: values?.positionTitle?.id
                                                }}
                                                getOptionLabel={(option) => {
                                                    if (!option) return "";

                                                    const name = option.displayName || "";
                                                    const code = option.staffCode ? ` - ${option.staffCode}` : "";
                                                    const position = option.currentPosition?.name
                                                        ? ` (${option.currentPosition.name})`
                                                        : "";

                                                    return `${name}${code}${position}`;
                                                }}
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <GlobitsPagingAutocomplete
                                                label="Mẫu đánh giá"
                                                name={"evaluationTemplate"}
                                                customData={"data"}
                                                displayData='name'
                                                api={paging}
                                                keyValue='value'
                                            />
                                        </Grid>
                                    </Grid>

                                </DialogContent>
                                <DialogActions className='p-4'>
                                    <div className='flex flex-space-between flex-middle'>
                                        <Button startIcon={<BlockIcon/>} variant='contained'
                                                className='mr-12 btn btn-secondary d-inline-flex' color='secondary'
                                                onClick={() => handleIsOpenSaveForm(false)}>
                                            {t("general.button.cancel")}
                                        </Button>
                                        <Button startIcon={<SaveIcon/>} className='mr-0 btn btn-primary d-inline-flex'
                                                variant='contained' color='primary' type='submit'>
                                            {t("general.button.save")}
                                        </Button>
                                    </div>
                                </DialogActions>
                            </Form>
                        </GlobitsPopupV2>
                    )
                }}
            </Formik>
        </>
    );
}
export default observer(EvaluationTicketFilter)