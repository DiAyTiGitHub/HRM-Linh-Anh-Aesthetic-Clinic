import { Button, ButtonGroup, Collapse, Grid, Tooltip } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import FilterListIcon from '@material-ui/icons/FilterList';
import SearchIcon from '@material-ui/icons/Search';
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import GlobitsPagingAutocompleteV2 from "../../common/form/GlobitsPagingAutocompleteV2";
import { pagingAllOrg } from "../Organization/OrganizationService";
import SelectDepartmentComponent from "../../common/SelectComponent/SelectDepartment/SelectDepartmentComponent";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import ImportExcelDialogDepartment from "./ImportExcelDialogDepartment";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import { useTheme } from "@material-ui/core/styles";
import NoteIcon from "@material-ui/icons/Note";
import DeleteIcon from "@material-ui/icons/Delete";
import GlobitsDateTimePicker from "../../common/form/GlobitsDateTimePicker";

const initialValues = {
    page: 1,
    rowsPerPage: 10,
    keyword: null,
    // department: null,
    organization: null,
}

function DepartmentToolbar() {
    const { departmentStore } = useStore();
    const { t } = useTranslation();
    const {
        updatePageData,
        handleEditDepartment,
        handleSetSearchObject,
        handleDeleteList,
        handleClose,
        shouldOpenImportExcelDialog,
        selectedDepartmentList,
        importExcel
    } = departmentStore;
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
    const isExtraSmall = useMediaQuery(theme.breakpoints.down("xs"));

    async function handleFilter(values) {
        handleSetSearchObject(values);
        await updatePageData();
    }

    const [isOpenFilter, setIsOpenFilter] = useState(false);

    function handleCloseFilter() {
        if (isOpenFilter) {
            setIsOpenFilter(false);
        }
    }

    function handleOpenFilter() {
        if (!isOpenFilter) {
            setIsOpenFilter(true);
        }
    }

    function handleTogglePopupFilter() {
        if (isOpenFilter) handleCloseFilter();
        else handleOpenFilter();
    }

    return (
        <Formik
            enableReinitialize
            initialValues={initialValues}
            onSubmit={handleFilter}
        >
            {({ resetForm, values, setFieldValue, setValues }) => {

                return (
                    <Form autoComplete="off" style={{ padding: '10px', width: '100%' }}>
                        <Grid container spacing={2}>
                            {!isExtraSmall && (<Grid item md={3} xs={12}>
                                <ImportExcelDialogDepartment
                                    t={t}
                                    handleClose={handleClose}
                                    open={shouldOpenImportExcelDialog}
                                />
                                <ButtonGroup
                                    color="container"
                                    aria-label="outlined primary button group"
                                >
                                    <Button
                                        startIcon={<AddIcon />}
                                        onClick={() => handleEditDepartment()}
                                    >
                                        {!isMobile && t("general.button.add")}
                                    </Button>

                                    <Button
                                        disabled={selectedDepartmentList.length === 0}
                                        startIcon={<DeleteOutlineIcon />}
                                        onClick={() => {
                                            handleDeleteList();
                                        }}
                                    >
                                        {!isMobile && t("general.button.delete")}
                                    </Button>
                                </ButtonGroup>
                            </Grid>)}
                            {isExtraSmall && (<>
                                <Grid item lg={3} md={3} sm={4} xs={3}>
                                    <Button
                                        className="btn btn-info d-inline-flex"
                                        startIcon={<AddIcon />}
                                        variant="contained"
                                        onClick={() => {
                                            handleEditDepartment();
                                        }}
                                        fullWidth
                                    ></Button>
                                </Grid>
                                <Grid item sm={4} xs={3}>
                                    <Button
                                        className="btn btn-danger d-inline-flex"
                                        startIcon={<NoteIcon />}
                                        variant="contained"
                                        onClick={importExcel}
                                        fullWidth
                                    ></Button>
                                </Grid>
                                <ImportExcelDialogDepartment
                                    t={t}
                                    handleClose={handleClose}
                                    open={shouldOpenImportExcelDialog}
                                />
                                <Grid item sm={4} xs={3}>
                                    {selectedDepartmentList.length > 0 && (
                                        <Button
                                            className="btn btn-warning d-inline-flex"
                                            variant="contained"
                                            startIcon={<DeleteIcon />}
                                            onClick={() => {
                                                handleDeleteList();
                                            }}
                                            fullWidth
                                        ></Button>
                                    )}
                                </Grid>
                            </>)}
                            <Grid container spacing={2} md={9} xs={12} className={"flex justify-end align-center"}>
                                <Grid item xs={12} sm={6} md={5}>
                                    <div className="flex flex-center w-100">
                                        <Grid container spacing={2}>
                                            <Grid item xs={4} sm={4} md={4}>
                                                <div className="flex items-center h-100 flex-end">
                                                    <p className="no-wrap-text">
                                                        <b>Chọn đơn vị:</b>
                                                    </p>
                                                </div>
                                            </Grid>

                                            <Grid item xs={8} sm={8} md={8}>
                                                <GlobitsPagingAutocompleteV2
                                                    name="organization"
                                                    api={pagingAllOrg}
                                                    handleChange={(_, value) => {
                                                        setFieldValue("organization", value);
                                                    }}
                                                    style={{ width: "100%" }}
                                                />
                                            </Grid>
                                        </Grid>
                                    </div>
                                </Grid>
                                <Grid item xs={12} sm={6} md={4}>
                                    <Tooltip placement="top" title="Tìm kiếm theo mã, tên yêu cầu tuyển dụng">
                                        <GlobitsTextField
                                            placeholder="Tìm kiếm theo mã, tên yêu cầu tuyển dụng..."
                                            name="keyword"
                                            variant="outlined"
                                            notDelay
                                            fullWidth
                                        />
                                    </Tooltip>
                                </Grid>
                                <Grid item xs={12} sm={6} md={"auto"}>
                                    <Button
                                        startIcon={<SearchIcon />}
                                        className="py-2 px-8 btnHrStyle"
                                        type="submit"
                                    >
                                        Tìm kiếm
                                    </Button>
                                </Grid>
                            </Grid>
                        </Grid>
                        {/*<Collapse in={isOpenFilter} className="filterPopup">*/
                        }
                        {/*    <div className="flex flex-column">*/
                        }
                        {/*        <Grid container spacing={2}>*/
                        }
                        {/*            <Grid item xs={12}>*/
                        }
                        {/*                <div className="filterContent pt-8">*/
                        }
                        {/*                    <Grid container spacing={2}>*/
                        }
                        {/*                        <Grid item xs={12} className="pb-0">*/
                        }
                        {/*                            <p className="m-0 p-0 borderThrough2">*/
                        }
                        {/*                                Thông tin phòng ban*/
                        }
                        {/*                            </p>*/
                        }
                        {/*                        </Grid>*/
                        }
                        {/*                        <Grid item xs={12} sm={6} md={4} lg={3}>*/
                        }
                        {/*                            <GlobitsPagingAutocompleteV2*/
                        }
                        {/*                                name="organization"*/
                        }
                        {/*                                label="Đơn vị"*/
                        }
                        {/*                                api={pagingAllOrg}*/
                        }
                        {/*                                handleChange={(_, value) => {*/
                        }
                        {/*                                    setFieldValue("organization", value);*/
                        }
                        {/*                                    setFieldValue("department", null);*/
                        }
                        {/*                                    setFieldValue("position", null);*/
                        }
                        {/*                                }}*/
                        }
                        {/*                            />*/
                        }
                        {/*                        </Grid>*/
                        }

                        {/*                        <Grid item xs={12} sm={6} md={4} lg={3}>*/
                        }
                        {/*                            <SelectDepartmentComponent*/
                        }
                        {/*                                organizationId={values?.organization?.id}*/
                        }
                        {/*                                name={"department"}*/
                        }
                        {/*                                clearFields={["position"]}*/
                        }
                        {/*                            />*/
                        }
                        {/*                        </Grid>*/
                        }

                        {/*<Grid item xs={12} sm={6} md={4} lg={3}>*/
                        }
                        {/*    <GlobitsPagingAutocompleteV2*/
                        }
                        {/*        label={"Vị trí tuyển dụng"}*/
                        }
                        {/*        validate*/
                        }
                        {/*        name="position"*/
                        }
                        {/*        api={pagingPosition}*/
                        }
                        {/*        //disabled={!values?.department?.id}*/
                        }
                        {/*        searchObject={{*/
                        }
                        {/*            pageIndex: 1, pageSize: 9999, keyword: "",*/
                        }
                        {/*            departmentId: values?.department?.id*/
                        }
                        {/*        }}*/
                        }
                        {/*    />*/
                        }
                        {/*</Grid>*/
                        }
                        {/*        </Grid>*/
                        }

                        {/*        <div className="py-8 mt-12 border-bottom-fade border-top-fade">*/
                        }
                        {/*            <div className="flex justify-end">*/
                        }
                        {/*                <ButtonGroup*/
                        }
                        {/*                    color="container"*/
                        }
                        {/*                    aria-label="outlined primary button group"*/
                        }
                        {/*                >*/
                        }
                        {/*                    <Button*/
                        }
                        {/*                        onClick={() => {*/
                        }
                        {/*                            handleFilter(initialValues)*/
                        }
                        {/*                        }}*/
                        }
                        {/*                        startIcon={<RotateLeftIcon/>}*/
                        }
                        {/*                    >*/
                        }
                        {/*                        Đặt lại*/
                        }
                        {/*                    </Button>*/
                        }
                        {/*                    <Button*/
                        }
                        {/*                        type="button"*/
                        }
                        {/*                        onClick={handleCloseFilter}*/
                        }
                        {/*                        startIcon={<HighlightOffIcon/>}*/
                        }
                        {/*                    >*/
                        }
                        {/*                        Đóng bộ lọc*/
                        }
                        {/*                    </Button>*/
                        }
                        {/*                </ButtonGroup>*/
                        }
                        {/*            </div>*/
                        }
                        {/*        </div>*/
                        }
                        {/*    </div>*/
                        }
                        {/*</Grid>*/
                        }
                        {/*</Grid>*/
                        }
                        {/*    </div>*/
                        }
                        {/*</Collapse>*/
                        }
                    </Form>
                )
                    ;
            }}
        </Formik>
    )
        ;
}

export default memo(observer(DepartmentToolbar));