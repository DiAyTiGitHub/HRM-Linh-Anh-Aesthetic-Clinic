import { observer } from "mobx-react";
import React , { memo , useState } from "react";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import { Form , Formik } from "formik";
import { Grid , Button , ButtonGroup , Tooltip } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import GetAppIcon from "@material-ui/icons/GetApp";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";

function DepartmentTypeToolbar() {
    const {departmentTypeStore} = useStore();
    const {t} = useTranslation();

    const {
        pagingDepartmentType ,
        handleOpenCreateEdit ,
        searchObject ,
        handleSetSearchObject ,
        listOnDelete ,
        handleDeleteList ,
        uploadFileExcel ,
        handleDownloadDepartmentTypeTemplate
    } = departmentTypeStore;

    async function handleFilter(values) {
        const newSearchObject = {
            ... values ,
            pageIndex:1 ,
        };
        handleSetSearchObject(newSearchObject);
        await pagingDepartmentType();
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

    const {
        isAdmin ,
    } = useStore().hrRoleUtilsStore;

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
                                <Grid item xs={12} lg={6}>
                                    {(isAdmin) && (
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
                                                startIcon={<GetAppIcon/>}
                                                onClick={handleDownloadDepartmentTypeTemplate}
                                            >
                                                Tải mẫu nhập
                                            </Button>
                                            <Button
                                                startIcon={<CloudUploadIcon/>}
                                                onClick={() => document.getElementById("fileExcel").click()}
                                            >
                                                {t("general.button.importExcel")}
                                            </Button>

                                            <Button
                                                disabled={listOnDelete?.length === 0}
                                                startIcon={<DeleteOutlineIcon/>}
                                                onClick={handleDeleteList}
                                            >
                                                {t("general.button.delete")}
                                            </Button>
                                        </ButtonGroup>
                                    )}
                                    <input
                                        type="file"
                                        id="fileExcel"
                                        style={{display:"none"}}
                                        onChange={uploadFileExcel}
                                    />
                                </Grid>
                                <Grid item xs={12} md={6}>
                                    <div className="flex justify-between align-center">
                                        <Tooltip placement="top" title="Tìm kiếm theo từ khóa..." arrow>
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

                                            {/* <Button
                                                startIcon={<FilterListIcon className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`} />}
                                                className=" d-inline-flex py-2 px-8 btnHrStyle"
                                                onClick={handleTogglePopupFilter}
                                            >
                                                Bộ lọc
                                            </Button> */}

                                        </ButtonGroup>
                                    </div>
                                </Grid>

                            </Grid>

                            {/* <DepartmentTypeFilter
                                isOpenFilter={isOpenFilter}
                                handleFilter={handleFilter}
                                handleCloseFilter={handleCloseFilter}
                            /> */}

                        </Grid>
                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(DepartmentTypeToolbar));