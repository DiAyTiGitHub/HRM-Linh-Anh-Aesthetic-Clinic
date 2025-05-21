import {observer} from "mobx-react";
import React, {memo, useEffect, useState} from "react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import {Form, Formik} from "formik";
import {Button, ButtonGroup, Grid, Tooltip} from "@material-ui/core";
import {useStore} from "app/stores";
import {useTranslation} from "react-i18next";
import FilterListIcon from '@material-ui/icons/FilterList';
import DeleteIcon from "@material-ui/icons/Delete";
import ProjectActivityInProjectFilter from "./ProjectFilter";
import localStorageService from "../../services/localStorageService";
import ConstantList from "../../appConfig";
import {useHistory} from "react-router-dom";

function ProjectToolbar() {
    const {projectStore} = useStore();
    const {t} = useTranslation();
    const history = useHistory();

    const initialFilterValues = {
        keyword: "",
        isFinished: false,
        startDate: null,
        endDate: null,
    };
    const {
        updatePageData,
        selectedProjectList,
        handleDeleteList,
        canCreateUpdateDeleteProject
    } = projectStore;

    useEffect(() => {
        updatePageData({keyword: "", pageIndex: 1, pageSize: 10});
    }, []);

    const [isTester, setTester] = useState(false);

    useEffect(() => {
        let roles =
            localStorageService
                .getLoginUser()
                ?.user?.roles?.map((item) => item.authority) || [];

        let auth = ["HR_TESTER"];

        if (roles.some((role) => auth.indexOf(role) !== -1)) {
            setTester(true);
        } else {
            setTester(false);
        }
    }, []);

    async function handleFilter(filter) {
        await updatePageData(filter);
        handleCloseFilter();
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
            initialValues={initialFilterValues}
            onSubmit={handleFilter}
        >
            {({resetForm, values, setFieldValue, setValues}) => {

                return (
                    <Form autoComplete="off">
                        <Grid item xs={12}>
                            <Grid container spacing={2}>
                                <Grid item xs={12} md={6}>
                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        {!isTester && canCreateUpdateDeleteProject() && <Button
                                            startIcon={<AddIcon/>}
                                            fullWidth
                                            onClick={() => history.push(ConstantList.ROOT_PATH + `timesheet/project/create`)}
                                        >
                                            {t("general.button.add")}
                                        </Button>}
                                        {selectedProjectList?.length > 0 && canCreateUpdateDeleteProject() && (
                                            <Button
                                                fullWidth
                                                startIcon={<DeleteIcon/>}
                                                onClick={handleDeleteList}
                                            >
                                                {t("general.button.delete")}
                                            </Button>
                                        )}
                                    </ButtonGroup>
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <div className="flex justify-between align-center">
                                        <Tooltip placement="top" title="Tìm kiếm theo mã, tên, từ khóa dự án...">
                                            <GlobitsTextField
                                                placeholder="Tìm kiếm theo mã, tên, từ khóa dự án..."
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
                                                    className={`filterRotateIcon ${isOpenFilter && 'onRotate'}`}/>}
                                                className=" d-inline-flex py-2 px-8 btnHrStyle"
                                                onClick={handleTogglePopupFilter}
                                            >
                                                Bộ lọc
                                            </Button>
                                        </ButtonGroup>
                                    </div>
                                </Grid>
                            </Grid>

                            <ProjectActivityInProjectFilter
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

export default memo(observer(ProjectToolbar));