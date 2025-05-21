import {
  Button,
  ButtonGroup,
  Grid,
  Icon,
  IconButton,
  makeStyles,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow
} from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import { formatDate } from "app/LocalFunction";
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import "react-toastify/dist/ReactToastify.css";
import StaffEducationHistoryPopupAdd from './Popup/StaffEducationHistoryPopupAdd';
const useStyles = makeStyles((theme) => ({
  root: {
    background: "#E4f5fc",
    padding: "10px 15px",
    borderRadius: "5px",
  },
  groupContainer: {
    width: "100%",
    "& .MuiOutlinedInput-root": {
      borderRadius: "0!important",
    },
  },
  tableContainer: {
    marginTop: "16px",
    overflowX: "auto",
    overflowY: "auto",
    maxHeight: "60vh",
    "& .MuiTableCell-body": {
      border: "1px solid #e9ecef",
      padding: 0,
      textAlign: "center",
    },
    "& .MuiTableCell-head": {
      padding: "10px",
      minWidth: "150px",
      border: "1px solid #e9ecef",
      textAlign: "center",
    },
  },
  tableHeader: {
    width: "100%",
    borderBottom: "1px solid #E3F2FD",
    marginBottom: "8px",
  },
  tableRow_Body: {
    height: "50px",
  },
}));
export default observer(function StaffEducationHistory() {

  const { t } = useTranslation();
  const classes = useStyles();

  const [openPopup, setOpenPopup] = useState(false);
  const [editable, setEditable] = useState(true);
  const [editableIndex, setEditableIndex] = useState(null);
  const [item, setItem] = useState(null);

  const { values, setFieldValue } = useFormikContext();
  let list = values?.educationHistory;

  const handleSubmitItem = (values) => {
    let newList = list ? list : [];
    if (!item) {
      newList.push(values);
    } else {
      newList = newList?.map((el, index) => {
        if (values?.index === index) {
          return values;
        }
        return el;
      });
    }

    setFieldValue("educationHistory", newList);
    setOpenPopup(false);
    setEditable(true);
  };

  return (
    <Grid container spacing={2}> 
      <Grid item xs={12}>
        <FieldArray
          name="educationHistory"
          render={(arrayHelpers) => (
            <div className={classes.groupContainer}>
              <ButtonGroup
                  color="container"
                  aria-label="outlined primary button group"
              >
                <Button
                    startIcon={<AddIcon/>}
                    type="button"
                    onClick={() => {
                      setOpenPopup(true);
                      setItem(null);
                    }}
                >
                  Thêm mới
                </Button>
              </ButtonGroup>
              <div className={classes.tableContainer}>
                {list?.length > 0 ? (
                  <Table style={{ tableLayout: "auto" }}>
                    <TableHead>
                      <TableRow>
                        <TableCell style={{ minWidth: "60px" }}>
                          {t("general.action")}
                        </TableCell>
                        <TableCell>{t("educationHistory.startDate")}</TableCell>
                        <TableCell>{t("educationHistory.endDate")}</TableCell>
                        <TableCell> 
                          {t("educationHistory.educationalInstitution")}
                        </TableCell>
                        <TableCell>{t("educationHistory.country")}</TableCell>
                        <TableCell>{t("educationHistory.speciality")}</TableCell>
                        <TableCell>
                          {t("educationHistory.formsOfTraining")}
                        </TableCell>
                        <TableCell>{t("educationHistory.degree")}</TableCell>
                        <TableCell>{t("educationHistory.note")}</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {list?.map((item, index) => (
                        <TableRow key={index} className={classes.tableRow_Body}>
                          <TableCell size="small">
                            {editable === false && editableIndex === index ? (
                              <IconButton size="small">
                                <Icon fontSize="small" color="primary">
                                  visibility
                                </Icon>
                              </IconButton>
                            ) : (
                              <>
                                <IconButton
                                  size="small"
                                  onClick={() => {
                                    setItem({ ...item, index });
                                    setOpenPopup(true);
                                    setEditable(false);
                                    setEditableIndex(index);
                                  }}
                                >
                                  <Icon fontSize="small" color="primary">
                                    edit
                                  </Icon>
                                </IconButton>
                                <IconButton
                                  size="small"
                                  onClick={() => arrayHelpers.remove(index)}
                                >
                                  <Icon fontSize="small" color="secondary">
                                    delete
                                  </Icon>
                                </IconButton>
                              </>
                            )}
                          </TableCell>

                          <TableCell colSpan={1}>
                            {formatDate("DD/MM/YYYY", item?.startDate)}
                          </TableCell>

                          <TableCell colSpan={1}>
                            {formatDate("DD/MM/YYYY", item?.endDate)}
                          </TableCell>

                          <TableCell colSpan={1}>
                            {item?.educationalInstitution?.name
                              ? item?.educationalInstitution?.name
                              : null}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {item?.country?.name ? item?.country?.name : null}
                          </TableCell>

                          <TableCell colSpan={1}>
                            {item?.speciality?.name ? item?.speciality?.name : null}
                          </TableCell>

                          <TableCell colSpan={1}>
                            {item?.educationType?.name
                              ? item?.educationType?.name
                              : null}
                          </TableCell>

                          <TableCell colSpan={1}>
                            {item?.educationDegree?.name
                              ? item?.educationDegree?.name
                              : null}
                          </TableCell>

                          <TableCell colSpan={1}>
                            {item?.description ? item?.description : ""}
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                ) : (
                  <h5 className="text-primary n-w">
                    Không có quá trình đào tạo nào
                  </h5>
                )}
              </div>
            </div>
          )}
        />
      </Grid>

      {openPopup && (
        <StaffEducationHistoryPopupAdd
          item={item}
          handleSubmit={handleSubmitItem}
          handleClose={() => {
            setOpenPopup(false);
            setEditable(true);
          }}
          open={openPopup}
          editable={editable}
        />
      )}
    </Grid>
  );
});
