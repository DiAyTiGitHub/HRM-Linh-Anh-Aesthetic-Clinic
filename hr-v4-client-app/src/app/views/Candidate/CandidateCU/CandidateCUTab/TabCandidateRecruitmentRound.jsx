import { Grid, Icon, IconButton, makeStyles, Tooltip } from "@material-ui/core";
import GlobitsTable from "app/common/GlobitsTable";
import { CandidateRecruitmentRoundResult, CandidateRecruitmentRoundStatusLabel } from "app/LocalConstants";
import { formatDate } from "app/LocalFunction";
import { useStore } from "app/stores";
import EvaluationCandiateRoundPopup
  from "app/views/Recruitment/RecruitmentCU/EvaluationCandidateRound/EvaluationCandiateRoundPopup";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import CandidateRecruitmentRoundCUForm from "../../CandidateRecruitmentRound/CandidateRecruitmentRoundCUForm";
import CheckIcon from "@material-ui/icons/Check";
import CandidateRecruitmentRoundPopup
  from "app/views/Recruitment/RecruitmentCU/CandidateRecruitmentRound/CandidateRecruitmentRoundPopup";

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
      },
    },
  },
  tableHeader:{
    width:"100%",
    borderBottom:"1px solid #ccc",
    marginBottom:"8px",
    "& th":{
      width:"calc(100vw / 4)",
      border:"1px solid #ccc",
      padding:"8px 0 8px 4px",
    },
  },
}));

function TabCandidateRecruitmentRound () {
  const {t} = useTranslation ();
  const classes = useStyles ();
  const {values, setFieldValue} = useFormikContext ();
  const {candidateRecruitmentRoundStore} = useStore ();
  const {
    totalPages,
    getCandidateRoundByCandidateId,
    listCandidateRecruitmentRound,
    handleSelectListDelete,
    openCreateEditPopup,
  } = candidateRecruitmentRoundStore;

  const {
    handleOpenCreateEdit,
    handleSelectRound,
    saveCandidateRecruitmentRound
  } = useStore ().candidateRecruitmentRoundStore;
  const {evaluationCandidateRoundStore} = useStore ();
  const {handleOpenFormEvaluationCandidateRound, openFormEvaluationCandidateRound} = evaluationCandidateRoundStore;

  const columns = [
    {
      title:t ("general.action"),
      minWidth:"100px",
      align:"center",
      render:(rowData) => (
          <div className='flex align-center justify-center'>
            <Tooltip arrow placement='top' title={"Chỉnh sửa ứng viên vòng phỏng vấn"}>
              <IconButton
                  className='ml-4'
                  size='small'
                  onClick={() => {
                    handleSelectRound (rowData);
                    handleOpenCreateEdit (rowData);
                  }}>
                <Icon fontSize='small' style={{color:"#3f51b5"}}>
                  edit
                </Icon>
              </IconButton>
            </Tooltip>
            <Tooltip arrow placement='top' title={"Đánh giá Ứng viên vòng phỏng vấn"}>
              <IconButton
                  className='ml-4'
                  size='small'
                  onClick={() => {
                    handleSelectRound (rowData);
                    handleOpenFormEvaluationCandidateRound (rowData?.id);
                  }}>
                <Icon fontSize='small' style={{color:"#3f51b5"}}>
                  assignment_turned_in
                </Icon>
              </IconButton>
            </Tooltip>
          </div>
      ),
    },
    {
      title:"Địa điểm phỏng vấn",
      field:"workplace",
      render:(rowData) => (
          <span>{rowData?.workplace?.name || ""}</span>
      ),
    },
    {
      title:"Ngày phỏng vấn",
      field:"actualTakePlaceDate",
      render:(rowData) => (
          <span>{rowData?.actualTakePlaceDate && formatDate ("DD/MM/YYYY HH:mm", rowData?.actualTakePlaceDate)}</span>
      ),
    },
    {
      title:"Vòng tuyển dụng",
      field:"recruitmentRound",
      render:(rowData) => <span>{rowData?.recruitmentRound?.name}</span>,
    },
    {
      title:"Trạng thái",
      field:"status",
      render:(rowData) => (
          <span>
                    {
                      <p className='m-0'>
                        {rowData?.status? CandidateRecruitmentRoundStatusLabel[rowData.status] : ""}
                      </p>
                    }
                </span>
      ),
    },
    {
      title:"Kết quả vòng phỏng vấn",
      field:"resultStatus",
      align:"center",
      render:(rowData) => (
          <span>
                    {
                      <p className='m-0'>
                        {rowData?.resultStatus? CandidateRecruitmentRoundResult[rowData.resultStatus] : ""}
                      </p>
                    }
                </span>
      ),
    },
    {
      title:"Vòng hiện tại",
      field:"current",
      align:"center",
      render:(rowData) =>
          rowData?.current? (
              <CheckIcon
                  fontSize='small'
                  style={{color:"green", justifyContent:"center", alignItems:"center", margin:0}}
              />
          ) : null,
    },
  ];

  const fetch = () => {
    if (values?.id) {
      getCandidateRoundByCandidateId (values?.id);
    }
  };

  const handleSubmit = async (value) => {
    await saveCandidateRecruitmentRound (value)
    await getCandidateRoundByCandidateId (values?.id);
  }

  useEffect (() => {
    fetch ();
  }, [values?.id]);

  return (
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <GlobitsTable
              // selection
              data={listCandidateRecruitmentRound}
              handleSelectList={handleSelectListDelete}
              columns={columns}
              // totalPages={totalPages}
              nonePagination
          />
        </Grid>
        {openFormEvaluationCandidateRound && <EvaluationCandiateRoundPopup readOnly/>}
        {openCreateEditPopup && <CandidateRecruitmentRoundPopup handleAfterSubmit={handleSubmit}/>}
      </Grid>
  );
}

export default memo (observer (TabCandidateRecruitmentRound));
