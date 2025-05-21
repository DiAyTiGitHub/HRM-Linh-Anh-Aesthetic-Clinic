import React, { useState } from "react";
import {
  Button,
  IconButton,
  Icon,
  makeStyles,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
} from "@material-ui/core";
import { useFormikContext, FieldArray } from "formik";
import { useTranslation } from "react-i18next";
import AddIcon from "@material-ui/icons/Add";
import PersonCertificatePopup from "./PersonCertificatePopup";
import moment from "moment";

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
    "& .MuiTableCell-body": {
      border: "1px solid #e9ecef",
      padding: 0,
    },
    "& .MuiTableCell-head": {
      padding: "10px",
      border: "1px solid #e9ecef",
    },
  },
  tableHeader: {
    width: "100%",
    borderBottom: "1px solid #E3F2FD",
    marginBottom: "8px",
    "& th": {
      textAlign: "center",
    },

    "& th:first-child": {
      width: "10% !important",
    },
  },

  tableBody: {
    "& td": {
      textAlign: "center",
      padding: "5px 0 !important",
    },
  },
}));

export default function PersonCertificate() {
  const { t } = useTranslation();
  const classes = useStyles();
  const { values, setFieldValue } = useFormikContext();
  const [openPopup, setOpenPopup] = useState(false);
  const [editable, setEditable] = useState(true);
  const [editableIndex, setEditableIndex] = useState(null);
  const [item, setItem] = useState(null);

  const list = values?.personCertificate;

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

    setFieldValue("personCertificate", newList);
    setOpenPopup(false);
    setEditable(true);
  };

  return (
    <>
      <FieldArray
        name="personCertificate"
        render={(arrayHelpers) => (
          <div className={classes.groupContainer}>
            <Button
              className="btn btn-primary d-inline-flex"
              startIcon={<AddIcon />}
              variant="contained"
              onClick={() => {
                setOpenPopup(true);
                setItem(null);
              }}
            >
              {t("general.Add.agreement")}
            </Button>
            <div className={classes.tableContainer}>
              {list?.length > 0 ? (
                <Table>
                  <TableHead className={classes.tableHeader}>
                    <TableRow>
                      <TableCell colSpan={1}> {t("general.action")}</TableCell>
                      <TableCell colSpan={1}>{t("certificate.name")}</TableCell>
                      <TableCell colSpan={1}>
                        {t("certificate.level")}
                      </TableCell>
                      <TableCell colSpan={1}>
                        {t("certificate.issueDate")}
                      </TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody className={classes.tableBody}>
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
                          {item?.certificate?.name
                            ? item?.certificate?.name
                            : ""}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {item?.level ? item?.level : ""}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {item?.issueDate
                            ? moment(item?.issueDate).format("DD/MM/YYYY")
                            : ""}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              ) : (
                <h5 className="text-primary n-w">
                  Không có chứng chỉ/chứng nhận nào!
                </h5>
              )}
            </div>
          </div>
        )}
      />

      {openPopup && (
        <PersonCertificatePopup
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
    </>
  );
}
