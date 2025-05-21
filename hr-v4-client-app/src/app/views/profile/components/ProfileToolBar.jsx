import { Button, Grid } from "@material-ui/core";
import SearchIcon from "@material-ui/icons/Search";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import localStorageService from "app/services/localStorageService";
import { useStore } from "app/stores";
import { getChartByPositionId } from "app/views/OrganizationalChartData/OrganizationalChartDataService";
import { pagingPositionByCurrentUser } from "app/views/Position/PositionService";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";

function ProfileToolBar({ setOrgChartData }) {
    const { values, setFieldValue, isSubmitting } = useFormikContext();
    const isAdmin = useMemo(() => {
        let roles = localStorageService.getLoginUser()?.user?.roles?.map((item) => item.authority) || [];
        let auth = ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"];
        return roles.some((role) => auth.indexOf(role) !== -1);
    }, []);

    const { currentStaff, handleOpenChangePassWord } = useStore().profileStore;
    const { staffId } = useParams();
    const [currentStaffId, setcurrentStaffId] = useState(null);
    useEffect(() => {
        console.log(currentStaff);
        if (staffId) {
            setcurrentStaffId(staffId);
        } else if (currentStaff.id) {
            setcurrentStaffId(currentStaff.id);
        }
    }, [staffId, currentStaff]);

    useEffect(() => {
        if (currentStaffId) {
            pagingPositionByCurrentUser({ pageIndex: 1, pageSize: 1, staffId: currentStaffId })
                .then(({ data }) => {
                    console.log(data);
                    if (data?.content && data?.content?.length > 0) {
                        const position = data?.content[0];
                        setFieldValue("position", position);
                        setFieldValue("positionId", position?.id);
                        setFieldValue("objectId", position?.id);
                        return position;
                    } else {
                        setFieldValue("position", null);
                        setFieldValue("positionId", null);
                        setFieldValue("objectId", null);
                    }
                })
                .then((data) => {
                    if (data) {
                        let dto = { positionId: data?.id, numberOfLevel: values?.numberOfLevel };
                        getChartByPositionId(dto).then(({ data }) => {
                            setOrgChartData(data);
                        });
                    }
                })
                .catch((err) => {
                    console.error(err);
                });
        }
    }, [currentStaffId]);
    return (
        <Grid container spacing={2}>
            <Grid item xs={3}>
                <GlobitsPagingAutocompleteV2
                    // required
                    label='Vị trí'
                    name='position'
                    api={pagingPositionByCurrentUser}
                    style={{ width: "100%" }}
                    searchObject={{
                        getOwn: !isAdmin,
                    }}
                    handleChange={(_, value) => {
                        setFieldValue("position", value);
                        setFieldValue("objectId", value?.id);
                        setFieldValue("positionId", value?.id);
                    }}
                    getOptionLabel={(option) => {
                        return option?.id
                            ? option?.staff
                                ? `${option?.name} - ${option?.staff?.displayName}`
                                : `${option?.name} - Vacant`
                            : "";
                    }}
                />
            </Grid>
            <Grid item xs={3}>
                <GlobitsNumberInput
                    // required
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
    );
}

export default memo(observer(ProfileToolBar));
