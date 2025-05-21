import {
  Button,
  ButtonGroup,
  Grid,
  makeStyles
} from "@material-ui/core";
import { Delete } from "@material-ui/icons";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from 'react-i18next';
import AddIcon from "@material-ui/icons/Add";
import { CandidateAttachment } from "app/common/Model/Candidate/CandidateAttachment";
import { toast } from "react-toastify";
import { downloadFile, uploadFile } from "app/views/HumanResourcesInformation/StaffLabourAgreementAttachmentService";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import { CandidateAttachmentType } from "app/LocalConstants";
import { GetApp } from "@material-ui/icons";

const useStyles = makeStyles (() => ({
  root:{
    background:"#E4f5fc",
    padding:"10px 15px",
    borderRadius:"5px",
  },
  groupContainer:{
    width:"100%",
    "& .MuiOutlinedInput-root":{
      borderRadius:"0!important",
    },
  },
  tableContainer:{
    marginTop:"2px",
    overflowX:"auto",
    overflowY:"hidden",
    "& table":{
      border:"1px solid #ccc",
      borderCollapse:"collapse",
      "& td":{
        border:"1px solid #ccc",
      }
    }
  },
  tableHeader:{
    width:"100%",
    borderBottom:"1px solid #ccc",
    marginBottom:"8px",
    "& th":{
      width:"calc(100vw / 4)",
      border:"1px solid #ccc",
      padding:"8px 0 8px 4px"
    },
  },
}));

function TabCandidateAttachment () {
  const classes = useStyles ();
  const {values, setFieldValue} = useFormikContext ();

  const handleFileChange = async (event) => {
    if (!event?.target?.files) {
      toast.info ("Vui lòng chọn tệp/tài liệu cần tải lên!");
      return;
    }

    const newListCandidateAttachment = [];
    try {
      for (const file of event?.target?.files) {
        const res = await uploadFile (file);

        const newLine = new CandidateAttachment ();
        newLine.file = res.data;
        newLine.candidateId = values?.id;

        newListCandidateAttachment.push (newLine)
      }
    } catch (error) {
      console.error (error);
      toast.error ("Upload tệp/tài liệu không thành công!");
    }

    setFieldValue ('candidateAttachments', [... (values?.candidateAttachments || []), ... newListCandidateAttachment])
  }

  return (
      <Grid container spacing={2}>
        <FieldArray name="candidateAttachments">
          {({remove, push}) => (
              <>
                <Grid item xs={12}>
                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <ButtonGroup
                          color="container"
                          aria-label="outlined primary button group"
                      >
                        <Button
                            startIcon={<AddIcon/>}
                            type="button"
                            component="label"
                        >
                          Thêm tệp/tài liệu
                          <input type="file" onChange={handleFileChange} multiple hidden/>
                        </Button>
                      </ButtonGroup>
                    </Grid>
                  </Grid>
                </Grid>

                <Grid item xs={12} style={{overflowX:"auto"}}>
                  <section className={classes.tableContainer}>
                    <table className={classes.table}>
                      <thead>
                      <tr className={classes.tableHeader}>
                        <th width="10%">Thao tác</th>
                        <th>File name</th>
                        <th width="30%">Tên</th>
                        <th width="30%">Loại tệp/tài liệu</th>
                        <th width="10%">Ghi chú</th>
                      </tr>
                      </thead>
                      <tbody>
                      {values?.candidateAttachments?.length > 0? (
                          values?.candidateAttachments?.map ((candidateAttachment, index) => (
                              <CandidateAttachmentRow
                                  key={index}
                                  index={index}
                                  candidateAttachments={values?.candidateAttachments}
                                  nameSpace={`candidateAttachments[${index}]`}
                                  remove={() => remove (index)}
                                  push={() => push (index)}
                                  candidateAttachment={candidateAttachment}
                                  //  disabled={!hasEditPermission}
                              />
                          ))
                      ) : (
                          <tr className='row-table-body row-table-no_data'>
                            <td colSpan={5} align='center' className="py-8">Chưa có thông tin</td>
                          </tr>
                      )}
                      </tbody>
                    </table>
                  </section>
                </Grid>

              </>
          )}
        </FieldArray>
      </Grid>
  )
}

export default memo (observer (TabCandidateAttachment));


const CandidateAttachmentRow = memo (({index, remove, nameSpace, disabled, candidateAttachment}) => {
  const {t} = useTranslation ();

  const withNameSpace = (field) => {
    if (field) return `${nameSpace}.${field}`
    return nameSpace;
  }

  const [openConfirmDeletePopup, setOpenConfirmDeletePopup] = useState (false);

  function handleConfirmDeleteItem () {
    remove ();
  }

  return (
      <>
        <tr className='row-table-body' key={index}>
          <td align='center'>
                    <span
                        className="pointer tooltip"
                        style={{cursor:'pointer'}}
                        onClick={() => downloadFile (candidateAttachment.file.id).then (res => {
                          const blob = res.data;
                          const url = window.URL.createObjectURL (blob);
                          const a = document.createElement ('a');
                          a.href = url;
                          a.download = candidateAttachment?.file?.name;
                          document.body.appendChild (a);
                          a.click ();
                          a.remove ();
                          window.URL.revokeObjectURL (url);
                        })
                            .catch (error => {
                              toast.error ('Lỗi tải tệp/tài liệu:', error);
                            })
                        }
                    >
                        <GetApp className="text-primary"/>
                    </span>

            {!disabled &&
                <span
                    className="pointer tooltip"
                    style={{cursor:'pointer'}}
                    onClick={() => setOpenConfirmDeletePopup (true)}
                >
                            <Delete className="text-red"/>
                        </span>
            }
          </td>

          <td>{candidateAttachment?.file?.name}</td>

          <td>
            <GlobitsTextField name={withNameSpace ("name")}/>
          </td>
          <td>
            <GlobitsSelectInput options={CandidateAttachmentType.getListData ()}
                                name={withNameSpace ("attachmentType")}/>
          </td>
          <td>
            <GlobitsTextField name={withNameSpace ("note")}/>
          </td>
        </tr>

        <GlobitsConfirmationDialog
            open={openConfirmDeletePopup}
            onConfirmDialogClose={() => setOpenConfirmDeletePopup (false)}
            onYesClick={handleConfirmDeleteItem}
            title={t ("confirm_dialog.delete.title")}
            text={"Bạn có chắc muốn tệp đính kèm này không?"}
            agree={t ("confirm_dialog.delete.agree")}
            cancel={t ("confirm_dialog.delete.cancel")}
        />

      </>
  )
})
