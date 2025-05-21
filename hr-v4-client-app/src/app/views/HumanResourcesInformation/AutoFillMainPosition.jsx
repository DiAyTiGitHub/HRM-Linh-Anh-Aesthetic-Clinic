import { Grid } from "@material-ui/core";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { pagingAllDepartments } from "../Department/DepartmentService";
import { pagingAllOrg } from "../Organization/OrganizationService";
import { pagingPosition } from "../Position/PositionService";

import { t } from "app/common/CommonFunctions";

const AutoFillMainPosition = (props) => {
    const { values, setFieldValue } = useFormikContext();
    let { selectedPosition, required } = props;
    const staff = values?.staff;
    const staffId = staff?.id;

    const [searchFromPosition, setSearchFromPosition] = useState({});

    useEffect(() => {
        // set value cho form
        if (selectedPosition?.id) {
            setFieldValue("fromPosition", selectedPosition);
        } else {
            setFieldValue("fromPosition", null);
        }
        setFieldValue("startDate", new Date());
        // setFieldValue("staff", values?.staff);

        setFieldValue("transferType", 1);
        if (selectedPosition?.department) {
            setFieldValue("fromDepartment", selectedPosition?.department);
            setFieldValue("toDepartment", selectedPosition?.department);
            if (selectedPosition?.department?.organization) {
                setFieldValue("fromOrganization", selectedPosition?.department?.organization);
                setFieldValue("toOrganization", selectedPosition?.department?.organization);
            }
        }

        // set lai searchObj cho fromPosition
        if (staffId) {
            setSearchFromPosition({
                staffId: staffId,
            });
        }
    }, [selectedPosition]);

    const handleChangeFromPosition = (_, value) => {
        setFieldValue("fromPosition", value);
        setFieldValue("toPosition", null);
        console.log(value);
        if (value?.department) {
            setFieldValue("toDepartment", value?.department);
            setFieldValue("fromDepartment", value?.department);
            if (value?.department?.organization) {
                setFieldValue("fromOrganization", value?.department?.organization);
                setFieldValue("toOrganization", value?.department?.organization);
            }
        } else {
            setFieldValue("fromOrganization", null);
            setFieldValue("fromDepartment", null);
            setFieldValue("toOrganization", null);
            setFieldValue("toDepartment", null);
            setFieldValue("transferType", null);
        }
    };

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <GlobitsPagingAutocompleteV2
                    label={"Đơn vị hiện tại"}
                    name='fromOrganization'
                    api={pagingAllOrg}
                    getOptionLabel={(option) => option?.name || ""}
                    disabled
                />
            </Grid>
            <Grid item xs={12}>
                <GlobitsPagingAutocompleteV2
                    label={"Phòng ban hiện tại"}
                    name='fromDepartment'
                    api={pagingAllDepartments}
                    value={values.fromDepartment} // Thêm value để hiển thị
                    disabled
                    getOptionLabel={(option) => {
                        return option?.code ? `${option?.name} - ${option?.code}` : option?.name;
                    }}
                />
            </Grid>
            <Grid item xs={12}>
                <GlobitsPagingAutocompleteV2
                    label={t("staffWorkingHistory.fromPosition")}
                    name='fromPosition'
                    required={required}
                    api={pagingPosition}
                    searchObject={searchFromPosition}
                    handleChange={handleChangeFromPosition}
                />
            </Grid>
            <Grid item xs={12}>
                <GlobitsPagingAutocompleteV2
                    label={"Nhân viên phụ trách"}
                    name='staff'
                    value={values.staff} // Thêm value để hiển thị
                    disabled
                    getOptionLabel={(option) => option?.displayName || option?.name - option?.code || ""}
                />
            </Grid>
        </Grid>
    );
};

export default memo(observer(AutoFillMainPosition));
