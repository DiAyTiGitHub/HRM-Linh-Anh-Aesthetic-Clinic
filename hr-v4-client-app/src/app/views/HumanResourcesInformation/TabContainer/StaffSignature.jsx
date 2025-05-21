import {
  Button, ButtonGroup,
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
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import "react-toastify/dist/ReactToastify.css";
import StaffSignaturePopupAdd from "./Popup/StaffSignaturePopupAdd";
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


export default observer(function StaffSignature() {

  const { t } = useTranslation();
  const classes = useStyles();
  const [openPopup, setOpenPopup] = useState(false);
  const [editable, setEditable] = useState(true);
  const [editableIndex, setEditableIndex] = useState(null);
  const [item, setItem] = useState(null);

  const { values, setFieldValue } = useFormikContext();
  const list = values?.staffSignatures;

  const handleSubmitItem = (values) => {
    console.log(values);

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

    setFieldValue("staffSignatures", newList);
    setOpenPopup(false);
    setEditable(true);
    // setOpenPopup(false);
  };
  return (
    <React.Fragment>
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <FieldArray
            name="staffSignatures"
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
                            {t("Tên")}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {t("Mã")}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {t("Mô tả")}
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
                              {item?.name ? item?.name : ""}
                            </TableCell>
                            <TableCell colSpan={1}>
                              {item?.code ? item?.code : ""}
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
                      Không có chữ ký nào
                    </h5>
                  )}
                </div>
              </div>
            )}
          />
          {openPopup && (
            <StaffSignaturePopupAdd
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
