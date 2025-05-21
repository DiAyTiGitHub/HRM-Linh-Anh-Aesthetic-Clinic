import { Button, ButtonGroup, Checkbox, Grid, makeStyles, Tooltip } from "@material-ui/core";
import CheckIcon from "@material-ui/icons/Check";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";
import { bytesToKB } from "app/LocalFunction";
import { useStore } from "app/stores";
import { pagingHrDocumentTemplate } from "app/views/HrDocumentTemplate/HrDocumentTemplateService";
import SelectFile from "app/views/StaffDocumentItem/SelectFile";
import { Form, Formik, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router";
import { toast } from "react-toastify";
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

function StaffDocumentItemsV2 () {
  const {t} = useTranslation ();
  const classes = useStyles ();
  const {staffDocumentItemStore} = useStore ();
  const {id} = useParams ();
  const {pagingStaffDocumentItemByStaff, saveTemplateAndStaff, resetStore} = staffDocumentItemStore;

  const initialValues = {
    staffDocumentItems:[],
    documentTemplate:null,
    staffDocumentStatus:LocalConstants.StaffDocumentStatus.UNSUBMITTED.value,
  };

  const [formData, setFormData] = useState (initialValues);
  const [selectAll, setSelectAll] = useState (false); // Quản lý local state
  const [selectedRows, setSelectedRows] = useState ([]); // Quản lý local state

  const handleSubmitList = useCallback (
      async (values, {setFieldError}) => {
        if (!values.documentTemplate) {
          setFieldError ("documentTemplate", "Vui lòng chọn mẫu tài liệu!");
          toast.error ("Vui lòng chọn mẫu tài liệu trước khi lưu!");
          return;
        }
        const dto = {
          ... values,
          staff:{id},
          documentTemplate:{id:values?.documentTemplate?.id},
        };
        await saveTemplateAndStaff (dto);
      },
      [id]
  );

  useEffect (() => {
    pagingStaffDocumentItemByStaff ({staffId:id})
        .then ((result) => {
          setFormData (result);
          setSelectedRows ([]); // Reset local state khi dữ liệu thay đổi
          setSelectAll (false);
        })
        .catch ((err) => console.error ("Lỗi khi paging:", err));
    return resetStore;
  }, [id]);

  return (
      <Formik enableReinitialize initialValues={formData} onSubmit={handleSubmitList}>
        {({isSubmitting, values}) => (
            <Form autoComplete='off'>
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <StaffDocumentHeader
                      selectAll={selectAll}
                      setSelectAll={setSelectAll}
                      selectedRows={selectedRows}
                      setSelectedRows={setSelectedRows}
                  />
                </Grid>
                <StaffDocumentItemsList
                    selectAll={selectAll}
                    setSelectAll={setSelectAll}
                    selectedRows={selectedRows}
                    setSelectedRows={setSelectedRows}
                />
              </Grid>
            </Form>
        )}
      </Formik>
  );
}

function StaffDocumentHeader ({selectAll, setSelectAll, selectedRows, setSelectedRows}) {
  const {t} = useTranslation ();
  const classes = useStyles ();
  const {values, setFieldValue} = useFormikContext ();
  const {staffDocumentItemStore} = useStore ();
  const {id} = useParams ();

  const {setStaffId, setTemplateId, pagingStaffDocumentItemByStaff} = staffDocumentItemStore;

  const handleChangeTemplate = useCallback (
      (_, value) => {
        const oldTemplate = values?.documentTemplate;
        if (value?.id !== oldTemplate?.id) {
          setTemplateId (value?.id);
          setFieldValue ("documentTemplate", value);
          if (value) {
            pagingStaffDocumentItemByStaff ({staffId:id, hrDocumentTemplateId:value?.id})
                .then ((result) => {
                  setFieldValue ("staffDocumentItems", result.staffDocumentItems);
                  setSelectedRows ([]); // Reset local state khi thay đổi template
                  setSelectAll (false);
                })
                .catch ((err) => console.error ("Lỗi khi paging:", err));
          }
        }
        if (!value) {
          setFieldValue ("documentTemplate", null);
        }
      },
      [values?.documentTemplate, setFieldValue, setTemplateId, pagingStaffDocumentItemByStaff, setSelectedRows, setSelectAll]
  );

  const handleChangeSubmittedStatus = useCallback (
      (isSubmitted) => {
        if (!selectedRows.length) return; // Không làm gì nếu không có hàng nào được chọn
        selectedRows.forEach ((id) => {
          const index = values?.staffDocumentItems.findIndex ((item) => item.id === id);
          if (index !== -1) {
            setFieldValue (`staffDocumentItems[${index}].isSubmitted`, isSubmitted);
            setFieldValue (`staffDocumentItems[${index}].submissionDate`, isSubmitted? new Date () : null);
          }
        });
        setSelectedRows ([]); // Reset local state sau khi xử lý
        setSelectAll (false);
      },
      [selectedRows, setFieldValue, setSelectedRows, setSelectAll]
  );
  return (
      <Grid container spacing={2} className={classes.root}>
        <Grid item xs={3} className='pb-0'>
          <GlobitsPagingAutocompleteV2
              name='documentTemplate'
              label={t ("document_template")}
              api={pagingHrDocumentTemplate}
              handleChange={handleChangeTemplate}
              value={values?.documentTemplate}
              required
          />
        </Grid>
        <Grid item xs={3}>
          <GlobitsSelectInput
              label={t ("staff_document_status")}
              name='staffDocumentStatus'
              keyValue='value'
              value={values?.staffDocumentStatus}
              options={LocalConstants.StaffDocumentStatus.getListData ()}
          />
        </Grid>
        <Grid item xs={6} style={{display:"flex", justifyContent:"end", alignItems:"end"}}>
          <ButtonGroup aria-label='outlined primary button group'>
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
            <Tooltip placement='top' title={t ("save_documents")}>
              <Button className='btn btn-primary d-inline-flex' type='submit'>
                {t ("save_documents")}
              </Button>
            </Tooltip>
          </ButtonGroup>
        </Grid>
      </Grid>
  );
}

function StaffDocumentItemsList ({selectAll, setSelectAll, selectedRows, setSelectedRows}) {
  const {t} = useTranslation ();
  const {values} = useFormikContext ();

  const handleSelectAll = useCallback (() => {
    if (!values?.staffDocumentItems?.length) return; // Không làm gì nếu không có dữ liệu

    const allRowIds = values.staffDocumentItems.map ((item) => item.id);
    if (!selectAll) {
      setSelectedRows (allRowIds); // Chọn tất cả
      setSelectAll (true);
    } else {
      setSelectedRows ([]); // Bỏ chọn tất cả
      setSelectAll (false);
    }
  }, [selectAll, setSelectAll, setSelectedRows, values?.staffDocumentItems]);

  const handleSelectRow = useCallback (
      (id) => {
        setSelectedRows ((prev) => {
          const newSelected = prev.includes (id)
              ? prev.filter ((rowId) => rowId !== id) // Bỏ chọn
              : [... prev, id]; // Chọn thêm
          const allSelected = values?.staffDocumentItems.every ((item) => newSelected.includes (item.id));
          setSelectAll (allSelected);
          return newSelected;
        });
      },
      [setSelectedRows, setSelectAll, values?.staffDocumentItems]
  );

  return (
      <Grid item xs={12}>
        {values?.staffDocumentItems?.length? (
            <div className='table-root table-form'>
              <table className='table-container' cellPadding={0} cellSpacing={0}>
                <thead>
                <tr>
                  <th align='center' width='10%'>
                    <Checkbox checked={selectAll} onChange={handleSelectAll} color='primary'/>
                  </th>
                  <th align='center'>{t ("actions")}</th>
                  <th align='center' width='10%'>
                    {t ("no")}
                  </th>
                  <th align='center'>{t ("document_name")}</th>
                  <th align='center'>{t ("description")}</th>
                  <th align='center'>{t ("attachment")}</th>
                  <th align='center'>{t ("submission_date")}</th>
                  <th align='center' width='10%'>
                    {t ("submitted")}
                  </th>
                  <th align='center' width='10%'>
                    {t ("Bắt buộc")}
                  </th>
                </tr>
                </thead>
                <tbody>
                {values?.staffDocumentItems.map ((item, index) => (
                    <StaffDocumentItemRow
                        key={item.id}
                        index={index}
                        item={item}
                        nameSpace={`staffDocumentItems[${index}]`}
                        isSelected={selectedRows.includes (item.id)}
                        onSelectRow={() => handleSelectRow (item.id)}
                    />
                ))}
                </tbody>
              </table>
            </div>
        ) : (
            <p className='w-100 text-center'>{t ("no_documents")}</p>
        )}
      </Grid>
  );
}

const StaffDocumentItemRow = memo (({index, item, nameSpace, isSelected, onSelectRow}) => {
  const withNameSpace = (field) => {
    if (field) return `${nameSpace}.${field}`;
    return nameSpace;
  };

  const {setFieldValue, values} = useFormikContext ();
  const {staffDocumentItemStore} = useStore ();

  const handleChangeStatusStaffDocument = (updatedFile) => {
    // Sao chép danh sách staffDocumentItems hiện tại
    const staffDocumentItemList = [... (values?.staffDocumentItems || [])];

    // Cập nhật file cho hàng hiện tại trong bản sao
    staffDocumentItemList[index] = {
      ... staffDocumentItemList[index],
      file:updatedFile,
    };

    console.log ("Danh sách tài liệu sau khi cập nhật: ", staffDocumentItemList);

    if (staffDocumentItemList.length === 0) {
      setFieldValue ("staffDocumentStatus", LocalConstants.StaffDocumentStatus.UNSUBMITTED.value);
      console.log ("Không có tài liệu nào -> Trạng thái: Chưa nộp");
      return;
    }

    // Lọc danh sách tài liệu bắt buộc
    const requiredItems = staffDocumentItemList.filter ((item) => item?.documentItem?.isRequired);
    const totalRequiredFiles = requiredItems.length;
    const uploadedRequiredFiles = requiredItems.filter ((item) => item.file).length;
    const uploadedFiles = staffDocumentItemList.filter ((item) => item.file).length; // Tổng số file đã nộp

    console.log ("Tổng số tài liệu bắt buộc: ", totalRequiredFiles);
    console.log ("Số tài liệu bắt buộc đã nộp: ", uploadedRequiredFiles);
    console.log ("Tổng số tài liệu đã nộp (bắt buộc + không bắt buộc): ", uploadedFiles);

    if (uploadedFiles === 0) {
      // Nếu chưa nộp cái gì (kể cả file không bắt buộc)
      setFieldValue ("staffDocumentStatus", LocalConstants.StaffDocumentStatus.UNSUBMITTED.value);
      console.log ("Chưa nộp tài liệu nào -> Trạng thái: Chưa nộp");
    } else if (uploadedRequiredFiles < totalRequiredFiles) {
      // Nếu đã nộp ít nhất một file nhưng chưa đủ các file bắt buộc
      setFieldValue ("staffDocumentStatus", LocalConstants.StaffDocumentStatus.INCOMPLETED.value);
      console.log ("Đã nộp nhưng chưa đủ tất cả tài liệu bắt buộc -> Trạng thái: Thiếu");
    } else if (uploadedRequiredFiles === totalRequiredFiles) {
      // Nếu đã nộp hết các file bắt buộc
      setFieldValue ("staffDocumentStatus", LocalConstants.StaffDocumentStatus.COMPLETED.value);
      console.log ("Đã nộp đủ tất cả tài liệu bắt buộc -> Trạng thái: Hoàn thành");
    }
  };

  const handleAffterSubmitFile = useCallback (
      (file) => {
        // Cập nhật các trường liên quan đến file
        setFieldValue (withNameSpace ("file"), file);
        setFieldValue (withNameSpace ("isSubmitted"), Boolean (file));
        setFieldValue (withNameSpace ("submissionDate"), file? new Date () : null);

        // Gọi hàm cập nhật trạng thái với file vừa upload/xóa
        handleChangeStatusStaffDocument (file);
      },
      [index, setFieldValue, withNameSpace, values?.staffDocumentItems] // Thêm values.staffDocumentItems vào dependencies
  );

  return (
      <tr className='row-table-body'>
        <td align='center'>
          <Checkbox checked={isSelected} onChange={onSelectRow} color='primary'/>
        </td>
        <td align='center'>
          <div style={{display:"flex", justifyContent:"center", alignItems:"center"}}>
            <SelectFile
                name={withNameSpace ("file")}
                fileProp={item?.file}
                showPreview={true}
                showDowload={true}
                showDelete={true}
                showName={false}
                handleAffterSubmit={handleAffterSubmitFile}
                maxFileSize={5242880}
            />
          </div>
        </td>
        <td align='center'>{index + 1}</td>
        <td>
                <span style={{margin:"4px 10px", display:"block"}}>
                    {item?.documentItem?.name}
                  {item?.documentItem?.isRequired && <span style={{color:"red"}}>*</span>}
                </span>
        </td>
        <td>
          <span style={{margin:"4px 10px", display:"block"}}>{item?.documentItem?.description}</span>
        </td>
        <td>
          {item?.file? (
              <span style={{margin:"4px 10px", display:"block"}}>
                        {item?.file?.name} - {bytesToKB (item?.file?.contentSize)}
                    </span>
          ) : item?.documentItem?.isRequired? (
              <span style={{color:"red", fontSize:"12px", margin:"4px 10px", display:"block"}}>
                        * Cần bổ sung
                    </span>
          ) : null}
        </td>
        <td>
          <GlobitsDateTimePicker
              name={withNameSpace ("submissionDate")}
              maxDate={new Date ()}
              maxDateMessage='Ngày nộp không được lớn hơn ngày hiện tại'
              onChange={(date) => {
                item.submissionDate = date;
                setFieldValue (withNameSpace ("submissionDate"), date);
              }}
              value={item?.submissionDate}
          />
        </td>
        <td align='center'>
          <GlobitsCheckBox
              name={withNameSpace ("isSubmitted")}
              style={{justifyContent:"center", alignItems:"center", margin:0}}
              onChange={(e) => {
                item.isSubmitted = e.target.checked;
                setFieldValue (withNameSpace ("isSubmitted"), e.target.checked);
              }}
              checked={item.isSubmitted}
          />
        </td>
        <td align='center'>
          {item?.documentItem?.isRequired? (
              <CheckIcon
                  fontSize='small'
                  style={{color:"green", justifyContent:"center", alignItems:"center", margin:0}}
              />
          ) : null}
        </td>
      </tr>
  );
});

export default memo (observer (StaffDocumentItemsV2));
