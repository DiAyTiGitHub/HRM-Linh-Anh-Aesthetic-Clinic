import { Button, Grid } from "@material-ui/core";
import SearchIcon from "@material-ui/icons/Search";
import { t } from "app/common/CommonFunctions";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import localStorageService from "app/services/localStorageService";
import { pagingAllDepartments } from "app/views/Department/DepartmentService";
import { getChartByDepartmentId } from "app/views/OrganizationalChartData/OrganizationalChartDataService";
import { Form, Formik, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useMemo } from "react";
import * as Yup from "yup";

function DepartmentDiagramToolBar({ setOrgChartData }) {
    const { values, setFieldValue, isSubmitting } = useFormikContext();
    const isAdmin = useMemo(() => {
        let roles = localStorageService.getLoginUser()?.user?.roles?.map((item) => item.authority) || [];
        let auth = ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"];
        return roles.some((role) => auth.indexOf(role) !== -1);
    }, []);

    const initialValues = {
        position: {},
        positionId: "",
        numberOfLevel: 1,
    };

    const validationSchema = Yup.object().shape({
        position: Yup.object().required(t("validation.required")).nullable(),
        numberOfLevel: Yup.number().required(t("validation.required")).min(1, "Số nhỏ nhất là 1"),
    });
    const handleSubmit = (values) => {
        let dto = { ...values };

        if (values?.department) {
            dto = { ...dto, departmentId: values?.department?.id };
            setFieldValue("objectId", values?.department?.id);
        }

        getChartByDepartmentId(dto)
            .then(({ data }) => {
                console.log(data);
                setOrgChartData(data);
            })
            .catch((err) => {
                console.error(err);
            });
    };

    useEffect(() => {
        pagingAllDepartments({ pageIndex: 1, pageSize: 1, getOwn: !isAdmin })
            .then(({ data }) => {
                if (data?.content && data?.content?.length > 0) {
                    const department = data?.content[0];
                    setFieldValue("department", department);
                    setFieldValue("departmentId", department?.id);
                    setFieldValue("objectId", department?.id);
                    return department;
                } else {
                    setFieldValue("department", null);
                    setFieldValue("departmentId", null);
                    setFieldValue("objectId", null);
                }
            })
            .then((data) => {
                if (data) {
                    let dto = { departmentId: data?.id, numberOfLevel: values?.numberOfLevel };
                    getChartByDepartmentId(dto).then(({ data }) => {
                        setOrgChartData(data);
                    });
                }
            })
            .catch((err) => {
                console.error(err);
            });
    }, []);

    return (
        <Formik
            enableReinitialize
            initialValues={initialValues}
            validationSchema={validationSchema}
            onSubmit={handleSubmit}>
            {({ resetForm, values, setFieldValue, setValues }) => {
                return (
                    <Form autoComplete='off'>
                        <Grid container spacing={2}>
                            <Grid item xs={3}>
                                <GlobitsPagingAutocompleteV2
                                    required
                                    label='Phòng ban'
                                    name='department'
                                    api={pagingAllDepartments}
                                    style={{ width: "100%" }}
                                    searchObject={{
                                        getOwn: !isAdmin,
                                    }}
                                    onChange={(_, value) => {
                                        setFieldValue("department", value);
                                        setFieldValue("departmentId", value?.id);
                                        setFieldValue("objectId", value?.id);
                                    }}
                                />
                            </Grid>
                            <Grid item xs={3}>
                                <GlobitsNumberInput
                                    required
                                    label={"Số lượng cấp bậc hiển thị"}
                                    name='numberOfLevel'
                                    inputProps={{ maxLength: 12 }}
                                />
                            </Grid>
                            <Grid
                                item
                                xs={3}
                                style={{
                                    display: "flex",
                                    justifyContent: "start",
                                    alignItems: "end",
                                }}>
                                <Button
                                    startIcon={<SearchIcon />}
                                    className='mr-0 btn btn-primary d-inline-flex'
                                    variant='contained'
                                    color='primary'
                                    type='submit'>
                                    Cập nhật sơ đồ
                                </Button>
                            </Grid>
                        </Grid>
                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(DepartmentDiagramToolBar));
