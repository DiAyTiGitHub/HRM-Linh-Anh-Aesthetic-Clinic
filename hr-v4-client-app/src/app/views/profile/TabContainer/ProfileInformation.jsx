import { Grid, makeStyles } from "@material-ui/core";
import { observer } from "mobx-react";
import React from "react";
import "react-toastify/dist/ReactToastify.css";
import moment from "moment";
import { useFormikContext } from "formik";

const useStyles = makeStyles((theme) => ({
  root: {
    padding: '15px'
  },
  formControll: {
    display: 'flex',
    marginBottom: '8px',
    "& p": {
      marginTop: 0,
      marginLeft: '15px',
      marginBottom: 0
    }
  }
}));

export default observer(function ProfileInformation() {

  const { values } = useFormikContext();

  const classes = useStyles();

  return (
    <React.Fragment>
      <Grid container spacing={2}>
        <Grid item sm={6} xs={12}>
          <div className={classes.formControll}>
            <span className="text-muted">Trạng thái nhân viên: </span>
            <p>{values?.status?.name}</p>
          </div>
          <div className={classes.formControll}>
            <span className="text-muted">Phòng ban: </span>
            <p>{values?.department?.name}</p>
          </div>
          <div className={classes.formControll}>
            <span className="text-muted">Mã nhân viên: </span>
            <p>{values?.staffCode}</p>
          </div>
          {/* <div className={classes.formControll}>
            <span className="text-muted">Loại hợp đồng: </span>
            <p>{values?.labourAgreementType?.name}</p>
          </div> */}
          {/* <div className={classes.formControll}>
            <span className="text-muted">Ngày hợp đồng: </span>
            <p>{values?.contractDate ? moment(values?.contractDate).format("DD/MM/YYYY") : ""}</p>
          </div> */}
          <div className={classes.formControll}>
            <span className="text-muted">Ngày tuyển dụng: </span>
            <p>{values?.recruitmentDate ? moment(values?.recruitmentDate).format("DD/MM/YYYY") : ""}</p>
          </div>
          {/* <div className={classes.formControll}>
            <span className="text-muted">Chức danh chhuyên môn: </span>
            <p>{values?.professionalTitles}</p>
          </div> */}

        </Grid>
        <Grid item sm={6} xs={12}>
          {/* <div className={classes.formControll}>
            <span className="text-muted">Ngày về cơ quan hiện tại: </span>
            <p>{values?.startDate ? moment(values?.startDate).format("DD/MM/YYYY") : ""}</p>
          </div> */}
          {/* <div className={classes.formControll}>
            <span className="text-muted">Nghề nghiệp khi tuyển dụng: </span>
            <p>{values?.jobTitle}</p>
          </div> */}
          {/* <div className={classes.formControll}>
            <span className="text-muted">Hệ số phụ cấp: </span>
            <p>{values?.allowanceCoefficient}</p>
          </div> */}
          {/* <div className={classes.formControll}>
            <span className="text-muted">Ngày hưởng phụ cấp: </span>
            <p>{values?.dateOfReceivingAllowance ? moment(values?.dateOfReceivingAllowance).format("DD/MM/YYYY") : ""}</p>
          </div> */}
          {/* <div className={classes.formControll}>
            <span className="text-muted">Bậc lương: </span>
            <p>{values?.salaryLeve}</p>
          </div> */}
          {/* <div className={classes.formControll}>
            <span className="text-muted">Hệ số lương: </span>
            <p>{values?.salaryCoefficient}</p>
          </div> */}
          <div className={classes.formControll}>
            <span className="text-muted">Sổ BHXH: </span>
            <p>{values?.socialInsuranceNumber}</p>
          </div>
        </Grid>
      </Grid>
    </React.Fragment>
  );
});
