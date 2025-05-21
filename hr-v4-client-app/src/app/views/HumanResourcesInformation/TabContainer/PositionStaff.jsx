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
  TableRow,
} from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import LocalConstants from "app/LocalConstants";
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import moment from "moment";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import "react-toastify/dist/ReactToastify.css";
import PositionStaffPopup from "./Popup/PositionStaffPopup";
const useStyles = makeStyles((theme) => ({
  root: {
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
    "& th": {
      width: "calc(100vw / 4)",
    },
  },
}));


export default observer(function PositionStaff() {

  const { t } = useTranslation();
  const classes = useStyles();
  const [openPopup, setOpenPopup] = useState(false);
  const [editable, setEditable] = useState(true);
  const [editableIndex, setEditableIndex] = useState(null);
  const [item, setItem] = useState(null);

  const { values, setFieldValue } = useFormikContext();
  const list = values?.positions;

  const handleSubmitItem = (values) => {
    let newList = list ? list : [];
  
    if (!item) {
      // Nếu không có item cũ, thêm mới vào danh sách
      newList.push(values);
    } else {
      // Nếu có item cũ, cập nhật giá trị cho item tại index của values
      newList = newList?.map((el, index) => {
        if (values?.index === index) {
          // Nếu values.mainPosition là true, thì thiết lập mainPosition cho các mục còn lại là false
          if (values.mainPosition === true) {
            return { ...values, mainPosition: true };
          }
          return values;
        }
        // Nếu không phải item đang được cập nhật, đặt mainPosition = false
        return { ...el, mainPosition: false };
      });
    }
  
    //console.log("newList: ",newList);
    // Cập nhật lại giá trị trong Formik state
    setFieldValue("positions", newList);
  
    // Đóng popup và reset các trạng thái khác
    setOpenPopup(false);
    setEditable(true);
  };

  
  return (
    <React.Fragment>
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <FieldArray
            name="positions"
            render={(arrayHelpers) => (
              <div className={classes.groupContainer}>
                <ButtonGroup
                  color="container"
                  aria-label="outlined primary button group"
                >
                  <Button
                    startIcon={<AddIcon />}
                    onClick={() => {
                      setOpenPopup(true);
                      setItem(null);
                    }}
                  >
                    {t("general.button.add")}
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
                          <TableCell colSpan={1}>
                            {t("positionStaff.position")}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {t("positionStaff.relationshipType")}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {t("positionStaff.department")}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {t("positionStaff.supervisor")}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {t("positionStaff.fromDate")}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {t("positionStaff.toDate")}
                          </TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {list?.map((item, index) => (
                          <TableRow key={index}>
                            <TableCell>
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
                              {item?.position?.name
                                ? item?.position?.name
                                : null}
                            </TableCell>
                            <TableCell colSpan={1}>
                              {LocalConstants.RelationshipType.getListData().find(val => val.value == item?.relationshipType)?.name || ""}
                            </TableCell>
                            <TableCell colSpan={1}>
                              {item?.hrDepartment?.name
                                ? item?.hrDepartment?.name
                                : null}
                            </TableCell>
                            <TableCell colSpan={1}>
                              {item?.supervisor?.displayName
                                ? item?.supervisor?.displayName
                                : null}
                            </TableCell>
                            <TableCell colSpan={1}>
                              {item?.fromDate
                                ? moment(item?.fromDate).format("DD/MM/YYYY")
                                : null}
                            </TableCell>
                            <TableCell colSpan={1}>
                              {item?.toDate
                                ? moment(item?.toDate).format("DD/MM/YYYY")
                                : null}
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  ) : (
                    <p className="flex justify-center">
                      Không có quá trình công tác nào
                    </p>
                  )}
                </div>
              </div>
            )}
          />
          {openPopup && (
            <PositionStaffPopup
              item={item}
              handleSubmit={handleSubmitItem}
              open={openPopup}
              handleClose={() => {
                setOpenPopup(false);
                setEditable(true);
              }}
              editable={editable}
            />
          )}
        </Grid>
      </Grid>
    </React.Fragment>
  );
});
