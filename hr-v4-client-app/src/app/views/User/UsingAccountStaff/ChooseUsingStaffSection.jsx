import { Button, Grid, Tooltip } from "@material-ui/core";
import TouchAppIcon from "@material-ui/icons/TouchApp";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { pagingStaff } from "app/views/HumanResourcesInformation/StaffService";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import UsingStaffPopup from "./UsingStaffPopup";
import GlobitsPagingAutocompleteV2 from "../../../common/form/GlobitsPagingAutocompleteV2";

/*
 *   Select staff
 *   used: Tính lương 1 nhân viên
 */
function ChooseUsingStaffSection (props) {
  const {t} = useTranslation ();
  const {
    label,
    placeholder = "Chưa chọn nhân viên",
    required = false,
    disabled = false,
    readOnly = false,
    name = "staff",
    handleAfterSubmit,
    isBasic = false,
  } = props;

  const [openPopup, setOpenPopup] = useState (false);

  function handleClosePopup () {
    setOpenPopup (false);
  }

  const {values, setFieldValue} = useFormikContext ();

  return (
      <>
        {!disabled && (
            <Grid container spacing={1}>
              <Grid item xs={readOnly? 12 : 9}>
                <GlobitsPagingAutocompleteV2
                    name={name}
                    label={label}
                    api={pagingStaff}
                    required={required}
                    placeholder={placeholder}
                    // disabled={true}
                    readOnly={true}
                    getOptionLabel={(option) =>
                        option?.displayName && option?.staffCode
                            ? `${option.displayName} - ${option.staffCode}`
                            : option?.displayName || option?.staffCode || ""
                    }
                />
              </Grid>

              {!readOnly && (
                  <Grid item xs={3} className='flex align-end'>
                    <Tooltip placement='top' title='Chọn nhân viên'>
                      <Button
                          fullWidth
                          variant='contained'
                          className='btn bgc-lighter-dark-blue text-white d-inline-flex my-2'
                          // style={{ marginTop: "25px", }}
                          onClick={() => setOpenPopup (true)}
                          disabled={disabled}>
                        <TouchAppIcon className='text-white'/>
                      </Button>
                    </Tooltip>
                  </Grid>
              )}
            </Grid>
        )}

        {disabled && (
            <GlobitsPagingAutocompleteV2
                name={name}
                label={label || "Nhân viên"}
                api={pagingStaff}
                required={required}
                placeholder={placeholder}
                readOnly={true}
                getOptionLabel={(option) =>
                    option?.displayName && option?.staffCode
                        ? `${option.displayName} - ${option.staffCode}`
                        : option?.displayName || option?.staffCode || ""
                }
            />
        )}

        {openPopup && (
            <UsingStaffPopup
                open={openPopup}
                handleClose={handleClosePopup}
                disabled={disabled}
                name={name}
                handleAfterSubmit={handleAfterSubmit}
                isBasic={isBasic}
            />
        )}
      </>
  );
}

export default memo (observer (ChooseUsingStaffSection));
