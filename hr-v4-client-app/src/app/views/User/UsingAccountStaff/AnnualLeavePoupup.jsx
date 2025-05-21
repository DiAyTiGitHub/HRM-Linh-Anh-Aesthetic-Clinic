import { Button, ButtonGroup } from "@material-ui/core";
import AdjustIcon from "@material-ui/icons/Adjust";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import SearchTotalAnnualLeavePoupup from "./SearchTotalAnnualLeavePoupup";
function AnnualLeavePoupup(props) {
    const { t } = useTranslation();
    const { disabled = false, readOnly = false, name = "annualLeaveDays", handleAfterSubmit } = props;

    const [openPopup, setOpenPopup] = useState(false);

    function handleClosePopup() {
        setOpenPopup(false);
    }

    const { values, setFieldValue } = useFormikContext();

    return (
        <ButtonGroup color='container' aria-label='outlined primary button group'>
            {!disabled && (
                <Button startIcon={<AdjustIcon />} onClick={() => setOpenPopup(true)}>
                    Ngày nghỉ còn lại
                </Button>
            )}

            {openPopup && (
                <SearchTotalAnnualLeavePoupup
                    open={openPopup}
                    handleClose={handleClosePopup}
                    disabled={disabled}
                    name={name}
                    handleAfterSubmit={handleAfterSubmit}
                />
            )}
        </ButtonGroup>
    );
}

export default memo(observer(AnnualLeavePoupup));
