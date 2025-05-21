import React, {memo, useState} from "react";
import GlobitsTable from "app/common/GlobitsTable";
import {useStore} from "app/stores";
import {useTranslation} from "react-i18next";
import {Icon, IconButton, Tooltip} from "@material-ui/core";
import {observer} from "mobx-react";
import {formatDate} from "app/LocalFunction";
import {useHistory} from "react-router-dom";
import ConstantList from "app/appConfig";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import LocalConstants from "app/LocalConstants";
import ExamResultInEachRecruitmentRound from "./ExamResultInEachRecruitmentRound";

function ExamCandidateList() {
    const {examCandidateStore} = useStore();
    const {t} = useTranslation();

    const {
        listExamCandidates,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleSelectListChosen,
        getExamStatus,
        handleOpenRejectPopup,
        handleOpenPassPopup,
        handleOpenResetPopup,
        handleOpenFailPopup,
    } = examCandidateStore;

    const history = useHistory();

    function handleUpdateApplicant(applicant) {
        //link to new page like a staff
        history.push(ConstantList.ROOT_PATH + `candidate/` + applicant?.id + "?isFromExamCandidate=true");
    }

    const [anchorEl, setAnchorEl] = useState();
    const [selectedRow, setSelectedRow] = useState(null);

    const columns = [
        {
            title: t("general.action"),
            width: "10%",
            minWidth: "100px",
            align: "center",
            render: (rowData) => {
                return (
                    <div className="flex flex-middle justify-center">
                        <Tooltip title="Chi tiết" placement="top">
                            <IconButton size="small" onClick={function () {
                                handleUpdateApplicant(rowData);
                            }}>
                                <Icon fontSize="small" color="primary">
                                    remove_red_eye
                                </Icon>
                            </IconButton>
                        </Tooltip>

                        <Tooltip title="Thao tác khác" placement="top">
                            <IconButton
                                className="ml-8"
                                size="small"
                                onClick={(event) => {
                                    setSelectedRow(rowData);
                                    setAnchorEl(event?.currentTarget);
                                }}
                            >
                                <MoreHorizIcon/>
                            </IconButton>
                        </Tooltip>
                    </div>
                );
            },
        },
        {
            title: "Mã ứng viên",
            field: "candidateCode",
            minWidth: "150px",
        },
        {
            title: "Họ tên",
            minWidth: "150px",
            field: "displayName",
        },
        {
            title: "Ngày sinh",
            minWidth: "150px",
            field: "birthDate",
            render: (rowData) => (
                <span>
          {rowData?.birthDate && (formatDate("DD/MM/YYYY", rowData?.birthDate))}
        </span>
            ),
        },
        // {
        //   title: "Giới tính",
        //   field: "gender",
        //   render: rowData => (
        //     <span>
        //       {rowData?.gender === "M" ? "Nam" : rowData?.gender === "F" ? "Nữ" : ""}
        //     </span>
        //   )
        // },
        {
            title: "SĐT",
            minWidth: "150px",
            field: "phoneNumber",
            render: rowData => (
                <>
                    {rowData?.phoneNumber && (
                        <span className="pr-8">
              {rowData?.phoneNumber}
            </span>
                    )}
                </>
            )
        },
        {
            title: "Ngày nộp hồ sơ",
            minWidth: "150px",
            field: "submissionDate",
            render: (rowData) => (
                <span>
          {rowData?.submissionDate && (formatDate("DD/MM/YYYY", rowData?.submissionDate))}
        </span>
            ),
        },
        {
            title: "Đợt tuyển dụng",
            minWidth: "150px",
            field: "recruitment",
            render: rowData => (
                <>
                    {rowData?.recruitment && (
                        <span className="pr-8">
              {rowData?.recruitment?.name}
            </span>
                    )}
                </>
            )
        },
        {
            title: "Vị trí ứng tuyển",
            minWidth: "150px",
            field: "position",
            render: rowData => (
                <>
                    {rowData?.position && (
                        <span className="pr-8">
              {rowData?.position?.name}
            </span>
                    )}
                </>
            )
        },
        {
            title: "Ngày thi tuyển",
            minWidth: "150px",
            field: "interviewDate",
            render: (rowData) => (
                <span>
          {rowData?.interviewDate && (formatDate("DD/MM/YYYY", rowData?.interviewDate))}
        </span>
            ),
        },
        {
            title: "Kết quả",
            minWidth: "150px",
            field: 'recruitmentRoundResults',
            render: (rowData) => {
                if (!rowData?.recruitmentRoundResults?.length || !Array.isArray(rowData?.recruitmentRoundResults)) {
                    return <b>Chưa có kết quả cho từng vòng</b>
                }

                return (
                    <React.Fragment>
                        {
                            rowData?.recruitmentRoundResults?.map((item, index) => {
                                return (
                                    <ExamResultInEachRecruitmentRound
                                        recruitmentRoundResult={item}
                                        rowData={rowData}
                                    />
                                );
                            })
                        }
                    </React.Fragment>
                );
            }
        },
        {
            title: "Đơn vị tuyển dụng",
            minWidth: "150px",
            field: "recruitment.organization.name",
            align: "left",
        },
        {
            title: "Phòng ban tuyển dụng",
            minWidth: "150px",
            field: "recruitment.department.name",
            align: "left",
        },
        {
            title: "Vị trí tuyển dụng",
            minWidth: "150px",
            field: "positionTitle.name",
            align: "left",
        },
    ];

    function handleClosePopover() {
        setAnchorEl(null);
        setSelectedRow(null);
    }

    if (searchObject?.examStatus == LocalConstants.CandidateExamStatus.REJECTED.value) {
        columns.splice(columns.length - 1, 0, {
            title: "Lý do từ chối",
            field: "refusalReason",
            minWidth: "150px",
        });
    }

    return (
        <>
            <GlobitsTable
                selection
                data={listExamCandidates}
                handleSelectList={handleSelectListChosen}
                columns={columns}
                totalPages={totalPages}
                handleChangePage={handleChangePage}
                setRowsPerPage={setPageSize}
                pageSize={searchObject?.pageSize}
                pageSizeOption={[10, 25, 50]}
                totalElements={totalElements}
                page={searchObject?.pageIndex}
            />

            {Boolean(anchorEl) && (
                <Menu
                    id={"simple-menu-options"}
                    anchorEl={anchorEl}
                    keepMounted
                    open={Boolean(anchorEl)}
                    onClose={handleClosePopover}
                    className="py-0"
                >
                    {/* ĐÁNH DẤU ỨNG VIÊN LÀ CHƯA DỰ THI */}
                    {selectedRow?.examStatus != LocalConstants.CandidateExamStatus.NOT_TESTED_YET.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListChosen([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenResetPopup();
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6" fontSize="small">
                                loop
                            </Icon>
                            Chưa dự thi
                        </MenuItem>
                    )}


                    {/*  Đánh dấu ứng viên đã thi PASS */}
                    {selectedRow?.examStatus != LocalConstants.CandidateExamStatus.PASSED.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListChosen([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenPassPopup();
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6" fontSize="small" style={{color: "green"}}>
                                done_all
                            </Icon>
                            Đạt
                        </MenuItem>
                    )}

                    {/* Đánh dấu ứng viên đã FAIL */}
                    {selectedRow?.examStatus != LocalConstants.CandidateExamStatus.FAILED.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListChosen([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenFailPopup();
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6" fontSize="small" style={{color: "red"}}>
                                close
                            </Icon>
                            Không đạt
                        </MenuItem>
                    )}

                    {/* Đánh dấu ứng viên đã bị từ chối */}
                    {selectedRow?.examStatus != LocalConstants.CandidateExamStatus.REJECTED.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListChosen([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenRejectPopup();
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6" fontSize="small" style={{color: "gray"}}>
                                thumb_down
                            </Icon>
                            Từ chối
                        </MenuItem>
                    )}
                </Menu>
            )}
        </>
    );
}

export default memo(observer(ExamCandidateList));
