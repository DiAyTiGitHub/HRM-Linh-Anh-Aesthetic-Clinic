import { observer } from "mobx-react";
import React , { memo , useEffect , useState } from "react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import { Form , Formik } from "formik";
import { Grid , Button , ButtonGroup , Tooltip } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import FilterListIcon from '@material-ui/icons/FilterList';
import StaffPositionFilter from "./HrDepartmentIpFilter";
import DeleteIcon from "@material-ui/icons/Delete";
import GetAppIcon from "@material-ui/icons/GetApp";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import HrDepartmentIpFilter from "./HrDepartmentIpFilter";

function HrDepartmentIpToolbar() {
    const {hrDepartmentIpStore , hrRoleUtilsStore} = useStore();
    const {t} = useTranslation();

    const {
        handleDeleteList ,
        pagingHrDepartmentIp ,
        handleOpenCreateEdit ,
        searchObject ,
        listOnDelete ,
        handleSetSearchObject
    } = hrDepartmentIpStore;

    function handlePreSubmit(values) {
        return {
            ... values ,
            departmentId:values?.department?.id || null ,
            organizationId:values?.organization?.id || null ,
        };
    }

    async function handleFilter(values) {
        const newSearchObject = {
            ... handlePreSubmit(values) ,
            pageIndex:1 ,
        };
        handleSetSearchObject(newSearchObject);
        await pagingHrDepartmentIp();
    }

    const [isOpenFilter , setIsOpenFilter] = useState(false);

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

    const {isManager , isAdmin} = hrRoleUtilsStore
    return (
        <Formik
            enableReinitialize
            initialValues={searchObject}
            onSubmit={handleFilter}
        >
            {({resetForm , values , setFieldValue , setValues}) => {

                return (
                    <Form autoComplete="off">
                        <Grid item xs={12}>
                            <Grid container spacing={2}>
                                <Grid item xs={12} md={6}>
                                    {(isManager || isAdmin) && (
                                        <ButtonGroup
                                            color="container"
                                            aria-label="outlined primary button group"
                                        >
                                            <Button
                                                startIcon={<AddIcon/>}
                                                onClick={() => handleOpenCreateEdit()}
                                            >
                                                {t("general.button.add")}
                                            </Button>
                                            <Button
                                                fullWidth
                                                startIcon={<DeleteIcon/>}
                                                onClick={handleDeleteList}
                                                disabled={listOnDelete?.length <= 0}
                                            >
                                                Xóa
                                            </Button>
                                        </ButtonGroup>
                                    )}
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <div className="flex justify-between align-center">
                                        <Tooltip placement="top" title="Tìm kiếm theo từ khóa...">
                                            <GlobitsTextField
                                                placeholder="Tìm kiếm theo từ khóa..."
                                                name="keyword"
                                                variant="outlined"
                                                notDelay
                                            />
                                        </Tooltip>

                                        <ButtonGroup
                                            className="filterButtonV4"
                                            color="container"
                                            aria-label="outlined primary button group"
                                        >
                                            <Button
                                                startIcon={<SearchIcon className={``}/>}
                                                className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                                                type="submit"
                                            >
                                                Tìm kiếm
                                            </Button>
                                            <Button
                                                startIcon={<FilterListIcon
                                                    className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`}/>}
                                                className=" d-inline-flex py-2 px-8 btnHrStyle"
                                                onClick={handleTogglePopupFilter}
                                            >
                                                Bộ lọc
                                            </Button>
                                        </ButtonGroup>
                                    </div>
                                </Grid>
                            </Grid>

                            <HrDepartmentIpFilter
                                isOpenFilter={isOpenFilter}
                                handleFilter={handleFilter}
                                handleCloseFilter={handleCloseFilter}
                            />
                        </Grid>
                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(HrDepartmentIpToolbar));