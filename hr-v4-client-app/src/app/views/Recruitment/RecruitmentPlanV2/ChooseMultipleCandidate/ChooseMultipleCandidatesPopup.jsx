import React , { memo , useEffect } from "react";
import { Checkbox , Grid , Tooltip , } from "@material-ui/core";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { useFormikContext } from "formik";
import GlobitsTable from "app/common/GlobitsTable";
import { formatDate } from "app/LocalFunction";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import LocalConstants from "app/LocalConstants";
import CandidateIndexToolbar from "./CandidateIndexToolbar";

function ChooseMultipleCandidatesPopup() {

    const {t} = useTranslation();
    const {candidateStore , recruitmentPlanStore} = useStore();

    const {
        listCandidates ,
        totalPages ,
        totalElements ,
        searchObject ,
        handleChangePage ,
        setPageSize ,
        pagingCandidates ,
        resetStore ,
    } = candidateStore;

    const {
        handleOpenChooseMultipleCandidatesPopup ,
        openChooseMultipleCandidatesPopup
    } = recruitmentPlanStore;
    const {values , setFieldValue} = useFormikContext();

    useEffect(function () {
        pagingCandidates();
        return resetStore;
    } , []);

    function handleSelectCandidate(chosenCandidate) {
        const isAlreadySelected = values?.candidates?.some((candidate) => candidate?.id === chosenCandidate.id);

        if (isAlreadySelected) {
            const updatedCandidates = values?.candidates?.filter((candidate) => candidate?.id !== chosenCandidate.id);
            setFieldValue("candidates" , updatedCandidates);
        } else {
            setFieldValue("candidates" , [... values.candidates , chosenCandidate]);
        }
    }

    const handleSelectAll = (checked) => {
        if (checked) {
            setFieldValue("candidates" , []);
        } else {
            setFieldValue("candidates" , listCandidates);
        }
    };

    const isCheckedAll = values?.candidates?.length === listCandidates?.length;

    const columns = [
        {
            title:(
                <Tooltip title={isCheckedAll ? "Bỏ chọn tất cả" : "Chọn tất cả"} placement="top">
                    <Checkbox
                        className="pr-16"
                        id="checkbox-all"
                        checked={isCheckedAll}
                        onClick={() => handleSelectAll(isCheckedAll)}
                    />
                </Tooltip>
            ) ,
            sorting:false ,
            align:"center" ,
            width:"10%" ,
            cellStyle:{
                textAlign:"center" ,
            } ,
            render:(rowData) => {
                const isChecked = values?.candidates?.some((candidate) => candidate?.id === rowData?.id);

                return (
                    <Tooltip title={isChecked ? "Bỏ chọn" : "Chọn sử dụng"} placement="top">
                        <Checkbox
                            className="pr-16"
                            id={`radio${rowData?.id}`}
                            name="radSelected"
                            value={rowData.id}
                            checked={isChecked}
                            onClick={() => handleSelectCandidate(rowData)}
                        />
                    </Tooltip>
                );
            }
        } ,
        {
            title:"Mã ứng viên" ,
            field:"candidateCode" ,
            minWidth:"150px" ,
        } ,
        {
            title:"Ứng viên" ,
            minWidth:"150px" ,
            render:(rowData) => (
                <>
                    {rowData.displayName && (
                        <p className='m-0'>
                            <strong>{rowData.displayName}</strong>
                        </p>
                    )}

                    {rowData.birthDate &&
                        <p className='m-0'>Ngày sinh: {formatDate("DD/MM/YYYY" , rowData.birthDate)}</p>}

                    {rowData.gender && <p className='m-0'>Giới
                        tính: {rowData.gender === "M" ? "Nam" : rowData.gender === "F" ? "Nữ" : ""}</p>}

                    {rowData.birthPlace && <p className='m-0'>Nơi sinh: {rowData.birthPlace}</p>}
                </>
            ) ,
        } ,
        {
            title:"Thông tin liên hệ" ,
            field:"info" ,
            minWidth:"150px" ,
            render:(rowData) => (
                <>
                    {rowData.phoneNumber && <p className='m-0'>SĐT: {rowData.phoneNumber}</p>}

                    {rowData.email && <p className='m-0'>Email: {rowData.email}</p>}
                </>
            ) ,
        } ,
        {
            title:"Ngày nộp hồ sơ" ,
            field:"submissionDate" ,
            minWidth:"150px" ,
            render:(rowData) => (
                <span>
                    {rowData?.submissionDate && (formatDate("DD/MM/YYYY" , rowData?.submissionDate))}
                </span>
            ) ,
        } ,

        {
            title:"Đợt tuyển dụng" ,
            field:"recruitment" ,
            minWidth:"150px" ,
            render:rowData => (
                <>
                    {rowData?.recruitment && (
                        <span className="pr-8">
                            {rowData?.recruitment?.name}
                        </span>
                    )}
                </>
            )
        } ,
        {
            title:"Trạng thái hồ sơ" ,
            field:"status" ,
            minWidth:"150px" ,
            render:function (applicant) {
                return (<span>{LocalConstants.CandidateStatus.getNameByValue(applicant?.status)}</span>);
            }
        } ,
        {
            title:"Đơn vị tuyển dụng" ,
            field:"organization.name" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,
        {
            title:"Phòng ban tuyển dụng" ,
            field:"department.name" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,
        {
            title:"Vị trí tuyển dụng" ,
            field:"positionTitle.name" ,
            align:"left" ,
            minWidth:"150px" ,
        } ,
    ];

    return (
        <GlobitsPopupV2
            noDialogContent
            open={openChooseMultipleCandidatesPopup}
            title='Danh sách ứng viên tham gia phỏng vến'
            size="md"
            scroll={"body"}
            onClosePopup={() => handleOpenChooseMultipleCandidatesPopup(false)}
        >
            <Grid container className="p-12">
                <div className="dialogScrollContent">
                    <Grid item xs={12}>
                        <CandidateIndexToolbar/>
                    </Grid>

                    <Grid item xs={12} className="pt-12">
                        <GlobitsTable
                            data={listCandidates}
                            columns={columns}
                            totalPages={totalPages}
                            handleChangePage={handleChangePage}
                            setRowsPerPage={setPageSize}
                            pageSize={searchObject?.pageSize}
                            pageSizeOption={[10 , 25 , 50]}
                            totalElements={totalElements}
                            page={searchObject?.pageIndex}
                        />
                    </Grid>
                </div>
            </Grid>
        </GlobitsPopupV2>
    );
}

export default memo(observer(ChooseMultipleCandidatesPopup));