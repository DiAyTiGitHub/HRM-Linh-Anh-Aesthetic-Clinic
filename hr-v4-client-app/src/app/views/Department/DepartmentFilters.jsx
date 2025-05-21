import React , { memo } from "react";
import { observer } from "mobx-react";
import { useStore } from "../../stores";
import { Form , Formik } from "formik";
import { Button , ButtonGroup , Grid , Tooltip } from "@material-ui/core";
import GlobitsCheckBox from "../../common/form/GlobitsCheckBox";
import GlobitsPagingAutocomplete from "../../common/form/GlobitsPagingAutocomplete";
import { pagingAllOrg } from "../Organization/OrganizationService";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";
import { useTranslation } from "react-i18next";

function DepartmentFilters(props) {
    const {departmentV2Store} = useStore();
    const {t} = useTranslation();

    const {
        pagingAllDepartment ,
        searchObject ,
        handleSetSearchObject ,
    } = departmentV2Store;

    async function handleFilter(values) {
        const newSearchObject = {
            ... values ,
            pageIndex:1 ,
        };
        handleSetSearchObject(newSearchObject);
        await pagingAllDepartment();
    }

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
                            <Grid container spacing={1} className={"flex justify-end"}>
                                <Grid item container spacing={1} xs={12} md={7} className={"flex justify-end"}>
                                    <Grid item xs={12} md={5} className={"flex justify-end"}>
                                        <GlobitsCheckBox
                                            label={"Cần bổ nhiệm quản lý"}
                                            name='isManager'
                                            checked={values?.isManager}
                                        />
                                    </Grid>
                                    <Grid item container spacing={1} xs={12} md={7}
                                          className={"flex justify-end"}>
                                        <Grid item xs={5}>
                                            <div
                                                className="flex justify-end align-center h-100 text-label-horizon">
                                                {t("department.organization")}
                                            </div>
                                        </Grid>

                                        <Grid item xs={7}>
                                            <Tooltip title={t("department.organization")} placement="top" arrow>
                                                <GlobitsPagingAutocomplete
                                                    name="organization"
                                                    api={pagingAllOrg}
                                                />
                                            </Tooltip>
                                        </Grid>
                                    </Grid>
                                </Grid>

                                <Grid item xs={12} md={5}>
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
                                        </ButtonGroup>
                                    </div>
                                </Grid>
                            </Grid>
                        </Grid>
                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(DepartmentFilters));
