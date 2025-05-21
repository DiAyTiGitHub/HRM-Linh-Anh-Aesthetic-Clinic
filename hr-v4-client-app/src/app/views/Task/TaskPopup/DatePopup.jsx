import React, { useEffect } from "react";
import { Button, Icon, IconButton } from "@material-ui/core";
import AccessTimeIcon from "@material-ui/icons/AccessTime";
import { useTranslation } from "react-i18next";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import "../_task.scss";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { useState } from "react";
import Popover from '@material-ui/core/Popover';
import { useFormikContext } from "formik";
import moment from "moment";

export default function DatePopup({ noEstimateHour, titleButton, classButton }) {
  const { t } = useTranslation();
  const [open, setOpen] = useState();
  const { values, setFieldValue } = useFormikContext();

  useEffect(() => {
    if (values.startTime && values.endTime) {
      setFieldValue("estimateHour", moment(values.endTime).diff(new Date(values.startTime), 'hours'))
    }
  }, [values.startTime, values.endTime]);

  return (
    <div>
      <Button
        className={classButton}
        variant="contained"
        startIcon={<AccessTimeIcon />}
        onClick={(event) => setOpen(event.currentTarget)}
        id={`datePopup`}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'center',
        }}
        transformOrigin={{
          vertical: 'top',
          horizontal: 'center',
        }}
      >
        {titleButton}
      </Button>
      <Popover
        anchorEl={open}
        open={Boolean(open)}
        onClose={() => setOpen(null)}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'center',
        }}
        transformOrigin={{
          vertical: 'top',
          horizontal: 'center',
        }}
      >
        <div className="p-8">
          <div
            className="pb-4 mb-4"
            style={{ position: 'relative', borderBottom: "1px solid #dee2e6" }}>
            <p style={{ textAlign: "center", margin: 0 }}>{t("task.time.title")}</p>
            <IconButton
              style={{ position: "absolute", right: "0", top: "0", padding: 0 }}
              onClick={() => setOpen(null)}
            >
              <Icon color="disabled" title={"Đóng"} fontSize="small" >
                close
              </Icon>
            </IconButton>
          </div>
          <div style={{ color: "#5e6c84" }}>{t("task.time.startTime")}</div>
          <GlobitsDateTimePicker name="startTime" isDateTimePicker />
          <div style={{ color: "#5e6c84" }}>{t("task.time.endTime")}</div>
          <GlobitsDateTimePicker name="endTime" isDateTimePicker />
          {!noEstimateHour ? (
            <>
              <div style={{ color: "#5e6c84" }}>{t("task.time.estimateHour")}</div>
              <GlobitsTextField name="estimateHour" type={'number'} />
            </>
          ) : (
            <></>
          )}
        </div>
      </Popover>
    </div>
  );
}
