import { Button, Grid } from "@material-ui/core";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { getChartByPositionId } from "app/views/OrganizationalChartData/OrganizationalChartDataService";
import { pagingPositionByCurrentUser } from "app/views/Position/PositionService";
import { useFormikContext } from "formik";
import SyncIcon from "@material-ui/icons/Sync";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import { useStore } from "app/stores";

function DiagramStaffToolBar({ setOrgChartData }) {
    const { values, setFieldValue } = useFormikContext();
    const { currentStaff } = useStore().profileStore;

    const { id } = useParams();
    const [currentStaffId, setcurrentStaffId] = useState(null);

    useEffect(() => {
        if (id) {
            setcurrentStaffId(id);
        } else if (currentStaff?.id) {
            setcurrentStaffId(currentStaff?.id);
        }
    }, [id, currentStaff, currentStaff?.id]);

    useEffect(() => {
        if (!currentStaffId) return;
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
    }, [currentStaffId, id]);

    return (
        <Grid container spacing={1}>
            <Grid item xs={6}>
                {console.log("currentStaff ", currentStaff)}
                <GlobitsPagingAutocompleteV2
                    // required
                    label='Vị trí'
                    name='position'
                    api={pagingPositionByCurrentUser}
                    style={{ width: "100%" }}
                    searchObject={{
                        staffId: currentStaffId,
                    }}
                    handleChange={(_, value) => {
                        setFieldValue("position", value);
                        setFieldValue("objectId", value?.id);
                        setFieldValue("positionId", value?.id);
                    }}
                    getOptionLabel={(option) => {
                        if (!option.id) return "";
                        return option?.staff
                            ? `${option?.name} - ${option?.staff?.displayName}`
                            : `${option?.name} - Vacant`;
                    }}
                />
            </Grid>
            <Grid item xs={6}>
                <div
                    style={{ width: "100%", alignItems: "end" }}
                    className='d-flex justify-content-end align-items-end'>
                    <div style={{ flex: 1 }}>
                        <GlobitsNumberInput
                            label={"Số lượng cấp"}
                            name='numberOfLevel'
                            inputProps={{ maxLength: 12 }}
                        />
                    </div>
                    <Button
                        startIcon={<SyncIcon />}
                        className='mr-0 btn btn-primary d-inline-flex'
                        variant='contained'
                        color='primary'
                        style={{ minHeight: "38px" }}
                        type='submit'></Button>
                </div>
            </Grid>
        </Grid>
    );
}

export default memo(observer(DiagramStaffToolBar));
