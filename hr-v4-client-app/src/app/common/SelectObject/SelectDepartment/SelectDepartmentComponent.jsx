import {Button, Grid, Tooltip,} from "@material-ui/core";
import {memo, useState} from "react";
// import "./RequestStyle.scss";
import {observer} from "mobx-react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import {useFormikContext} from "formik";
import {useTranslation} from "react-i18next";
import SelectDepartmentPopupV2 from "./SelectDepartmentPopupV2";
import TouchAppIcon from '@material-ui/icons/TouchApp';

function SelectDepartmentComponent(props) {
    const {
        organizationId = null,
        clearFields = [],
        name = "department",
        label = "Phòng ban",
        disabled = false,
        disabledTextFieldOnly = false,
        required = false,
    } = props;

    const {t} = useTranslation();

    const {
        values,
        setFieldValue
    } = useFormikContext();

    const [openDepartmentPopup, setOpenDepartmentPopup] = useState(false);

    // const handleConfirmSelectDepartment = () => {
    //     setOpenDepartmentPopup(false);
    // };

    function handelOpenDepartmentPopup() {
        setOpenDepartmentPopup(true);
    }

    function handleClosePopup() {
        setOpenDepartmentPopup(false);
    }

    return (
        // <div className="input-popup-container">
        <Grid container spacing={2}>
            <Grid item xs={9}>
                <GlobitsTextField
                    label={label}
                    name={name}
                    value={values[name] ? values[name]?.name : ""}
                    disabled={disabledTextFieldOnly}
                    required={required}
                />
            </Grid>

            <Grid item xs={3} className="flex align-end">
                <Tooltip placement="top" title="Chọn phòng ban">
                    <Button
                        fullWidth
                        variant="contained"
                        className="btn bgc-lighter-dark-blue text-white d-inline-flex my-2"
                        style={{marginTop: "25px",}}
                        onClick={() => handelOpenDepartmentPopup(true)}
                        disabled={disabled}
                    >
                        {/* {t("general.button.select")} */}
                        <TouchAppIcon className="text-white"/>
                    </Button>
                </Tooltip>
            </Grid>


            {openDepartmentPopup && (
                <SelectDepartmentPopupV2
                    open={openDepartmentPopup}
                    handleClosePopup={handleClosePopup}
                    setOpenDepartmentPopup={handelOpenDepartmentPopup}
                    organizationId={organizationId}
                    name={name}
                    clearFields={clearFields}
                />
            )}
        </Grid>


        // </div>
    );
}


export default memo(observer(SelectDepartmentComponent));


