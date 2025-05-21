import React, { memo, useEffect, useState } from "react";
import { observer } from "mobx-react";
import {
    Button,
    Grid,
    Tooltip,
} from "@material-ui/core";
import Paper from "@material-ui/core/Paper";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import TouchAppIcon from '@material-ui/icons/TouchApp';
import { useFormikContext } from "formik";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { pagingSalaryItem } from "app/views/Salary/SalaryItemV2/SalaryItemV2Service";
import LocalConstants from "app/LocalConstants";
import CRUDSalaryItemPopup from "./CRUDSalaryItemPopup";

function ChooseSalaryItemSection(props) {
    const { t } = useTranslation();
    const {
        label,
        required = false
    } = props;

    const [openPopup, setOpenPopup] = useState(false);

    function handleClosePopup() {
        setOpenPopup(false);
    }

    const { values, setFieldValue } = useFormikContext();

    const handleChange = (_, value) => {
        if (value?.calculationType) value.calculationType = Number(value?.calculationType);

        setFieldValue("salaryItem", value);
        setFieldValue("displayName", value?.name);
        setFieldValue("description", value?.description);

        if (value?.calculationType == LocalConstants.SalaryItemCalculationType.USING_FORMULA.value) {
            setFieldValue("usingFormula", value?.formula);
        }
    };


    return (
        <>
            <Grid container spacing={1}>
                <Grid item xs={9} sm={10}>
                    <GlobitsPagingAutocomplete
                        // autoFocus={values?.salaryItem == null ? true : false}
                        label="Thành phần lương"
                        name="salaryItem"
                        required
                        api={pagingSalaryItem}
                        onChange={handleChange}
                    />
                </Grid>

                <Grid item xs={3} sm={2} className="flex align-end">
                    <Tooltip placement="top" title="Chọn thành phần lương">
                        <Button
                            fullWidth
                            variant="contained"
                            className="btn bgc-lighter-dark-blue text-white d-inline-flex my-2"
                            // style={{ marginTop: "25px", }}
                            onClick={() => setOpenPopup(true)}
                        >
                            <TouchAppIcon className="text-white" />
                        </Button>
                    </Tooltip>
                </Grid>
            </Grid>

            {openPopup && (
                <CRUDSalaryItemPopup
                    open={openPopup}
                    handleCloseChooseItemPopup={handleClosePopup}
                />
            )}
        </>
    );
}

export default memo(observer(ChooseSalaryItemSection));
