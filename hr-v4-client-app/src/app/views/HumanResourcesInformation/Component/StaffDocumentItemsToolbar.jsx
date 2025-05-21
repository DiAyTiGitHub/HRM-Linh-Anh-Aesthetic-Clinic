import { Button, ButtonGroup, Grid, makeStyles, Tooltip } from "@material-ui/core";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";
import { useStore } from "app/stores";
import { pagingHrDocumentTemplate } from "app/views/HrDocumentTemplate/HrDocumentTemplateService";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useCallback, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router";
import "react-toastify/dist/ReactToastify.css";

const useStyles = makeStyles ((theme) => ({
  root:{
    "& .MuiAccordion-rounded":{borderRadius:"5px"},
    "& .MuiPaper-root":{borderRadius:"5px"},
    "& .MuiAccordionSummary-root":{
      borderRadius:"5px",
      color:"#5899d1",
      fontWeight:"400",
      "& .MuiTypography-root":{fontSize:"1rem"},
    },
    "& .Mui-expanded":{
      "& .MuiAccordionSummary-root":{
        backgroundColor:"#EBF3F9",
        color:"#5899d1",
        fontWeight:"700",
        maxHeight:"50px !important",
        minHeight:"50px !important",
      },
      "& .MuiTypography-root":{fontWeight:700},
    },
    "& .MuiButton-root":{borderRadius:"0.125rem !important"},
  },
  buttonGroupSpacing:{
    marginBottom:"10px",
  },
}));

function StaffDocumentItemsToolbar () {
  const {t} = useTranslation ();
  const classes = useStyles ();
  const {values, setFieldValue} = useFormikContext ();
  const {staffDocumentItemStore, staffStore} = useStore ();
  const {id} = useParams ();

  const {
    setStaffId,
    setTemplateId,
    pagingStaffDocumentItemByStaff,
    staffDocumentItemList,
    setStaffDocumentItemList,
    saveTemplateAndStaff,
    selectedRows,
    setSelectedRows,
    selectAll,
    setSelectAll,
  } = staffDocumentItemStore;

  const handleChangeTemplate = useCallback (
      (_, value) => {
        const oldTemplate = values?.documentTemplate;
        if (value?.id !== oldTemplate?.id) {
          setTemplateId (value?.id);
          setFieldValue ("documentTemplate", value);
          if (value) {
            pagingStaffDocumentItemByStaff ();
          }
        }
        if (!value) {
          setFieldValue ("documentTemplate", null);
        }
      },
      [values?.documentTemplate, setFieldValue, setTemplateId, pagingStaffDocumentItemByStaff, setStaffDocumentItemList]
  );

  const handleChangeSubmittedStatus = useCallback (
      (isSubmitted) => {
        const updatedList = [... staffDocumentItemList];
        selectedRows.forEach ((id) => {
          const index = updatedList.findIndex ((item) => item.id === id);
          if (index !== -1) {
            updatedList[index] = {
              ... updatedList[index],
              isSubmitted,
              submissionDate:isSubmitted? new Date () : null,
            };
            // Sync with Formik
            setFieldValue (`staffDocumentItems[${index}].isSubmitted`, isSubmitted);
            setFieldValue (`staffDocumentItems[${index}].submissionDate`, isSubmitted? new Date () : null);
          }
        });
        setStaffDocumentItemList (updatedList);
        setSelectedRows ([]);
        setSelectAll (false);
      },
      [selectedRows, staffDocumentItemList, setStaffDocumentItemList, setFieldValue]
  );

  const isFirstRender = useRef (true);

  useEffect (() => {
    if (isFirstRender.current) {
      isFirstRender.current = false;
      return;
    }

    if (values.staffDocumentStatus) {
      return;
    }

    if (staffDocumentItemList?.length > 0) {
      const requiredItems = staffDocumentItemList.filter ((item) => item?.documentItem?.isRequired);
      const uploadedRequiredFiles = requiredItems.filter ((item) => item.file).length;
      const totalRequiredFiles = requiredItems.length;

      if (uploadedRequiredFiles === 0 && totalRequiredFiles !== 0) {
        setFieldValue ("staffDocumentStatus", LocalConstants.StaffDocumentStatus.UNSUBMITTED.value);
      } else if (uploadedRequiredFiles < totalRequiredFiles) {
        setFieldValue ("staffDocumentStatus", LocalConstants.StaffDocumentStatus.INCOMPLETED.value);
      } else {
        setFieldValue ("staffDocumentStatus", LocalConstants.StaffDocumentStatus.COMPLETED.value);
      }
    } else {
      setFieldValue ("staffDocumentStatus", LocalConstants.StaffDocumentStatus.UNSUBMITTED.value);
    }
  }, [staffDocumentItemList, setFieldValue, values.staffDocumentStatus]);

  useEffect (() => {
    setStaffId (id);
    setTemplateId (values?.documentTemplate?.id);
    if (id && values?.documentTemplate?.id) {
      pagingStaffDocumentItemByStaff ();
    }
  }, [id, values?.documentTemplate?.id, setStaffId, setTemplateId, pagingStaffDocumentItemByStaff]);

  useEffect (() => {
    setSelectAll (staffDocumentItemList?.length > 0 && selectedRows.length === staffDocumentItemList.length);
  }, [selectedRows, staffDocumentItemList]);

  return (
      <Grid container spacing={2} className={classes.root}>
        <Grid item xs={3} className='pb-0'>
          <GlobitsPagingAutocompleteV2
              name='documentTemplate'
              label={t ("document_template")}
              api={pagingHrDocumentTemplate}
              handleChange={handleChangeTemplate}
              required
          />
        </Grid>
        <Grid item xs={3}>
          <GlobitsSelectInput
              label={t ("staff_document_status")}
              name='staffDocumentStatus'
              keyValue='value'
              options={LocalConstants.StaffDocumentStatus.getListData ()}
          />
        </Grid>
        <Grid item xl={6} style={{display:"flex", justifyContent:"start", alignItems:"end"}}>
          <ButtonGroup color='primary' aria-label='outlined primary button group'>
            <Tooltip placement='top' title={t ("mark_as_submitted")}>
              <Button disabled={selectedRows.length === 0} onClick={() => handleChangeSubmittedStatus (true)}>
                {t ("mark_as_submitted")}
              </Button>
            </Tooltip>
            <Tooltip placement='top' title={t ("mark_as_not_submitted")}>
              <Button disabled={selectedRows.length === 0} onClick={() => handleChangeSubmittedStatus (false)}>
                {t ("mark_as_not_submitted")}
              </Button>
            </Tooltip>
          </ButtonGroup>
        </Grid>
      </Grid>
  );
}

export default memo (observer (StaffDocumentItemsToolbar));
