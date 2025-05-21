import {
    Box,
    Button,
    ButtonGroup, Checkbox,
    Collapse,
    Grid,
    IconButton,
    makeStyles, MenuItem,
    Table, TableBody,
    TableCell,
    TableHead,
    TableRow, Typography, Menu
} from "@material-ui/core";
import {Add, Delete, RoundedCorner} from "@material-ui/icons";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsSelectInputV2 from "app/common/form/GlobitsSelectInputV2";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import {RecruitmentRound} from "app/common/Model/Recruitment/RecruitmentRound";
import {
    CandidateRecruitmentRoundResult,
    CandidateRecruitmentRoundStatusLabel, CandidateStatusEnum,
    RECRUITMENT_TYPE, RESULT_STATUS
} from "app/LocalConstants";
import {useStore} from "app/stores";
import {paging} from "app/views/System/SystemParam/Evaluation/EvaluationTemplate/EvaluationTemplateService";
import {FieldArray, getIn, useFormikContext} from "formik";
import {observer} from "mobx-react";
import React, {memo, useCallback, useMemo, useState} from "react";
import {useTranslation} from "react-i18next";
import ChooseMultipleCandidatesPopup
    from "../../RecruitmentPlanV2/ChooseMultipleCandidate/ChooseMultipleCandidatesPopup";
import RecruitmentPlanPersonParticipate from "../../RecruitmentPlanV2/RecruitmentPlanPersonParticipate";
import CandidateRoundListPopup from "./CandidateRoundListPopup";
import ExpandLessIcon from "@material-ui/icons/ExpandLess";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import GlobitsCheckBox from "../../../../common/form/GlobitsCheckBox";
import {formatDate} from "../../../../LocalFunction";
import {toast} from "react-toastify";
import ChooseUsingStaffSection from "../../../User/UsingAccountStaff/ChooseUsingStaffSection";
import AddIcon from "@material-ui/icons/Add";
import {doActionAssignment} from "../../../Candidate/CandidateRecruitmentRound/CandidateRecruitmentRoundService";
import CandidateRecruitmentRoundPopup from "../CandidateRecruitmentRound/CandidateRecruitmentRoundPopup";
import EditIcon from "@material-ui/icons/Edit";
import DeleteIcon from "@material-ui/icons/Delete";
import MoreVertIcon from "@material-ui/icons/MoreHoriz";
import GlobitsPagingAutocompleteV2 from "../../../../common/form/GlobitsPagingAutocompleteV2";
import {pagingWorkplace} from "../../../Workplace/WorkplaceService";

const useStyles = makeStyles({
    table: {
        minWidth: 650,
        "& .MuiTableCell-root": {
            border: "2px solid rgba(224, 224, 224, 1)"
        },
        tableLayout: 'unset'
    },
    fitWidthCell: {
        width: 'fit-content',
    },
});

const TabRecruitmentRound = () => {
    const {t} = useTranslation();
    const classes = useStyles();
    const {values, setFieldValue} = useFormikContext();
    const [choose, setChoose] = React.useState(null);
    const [open, setOpen] = React.useState(null);
    const [openCandidateList, setOpenCandidateList] = React.useState(false);

    function handleAddNewRow(push) {
        const newItem = new RecruitmentRound();
        newItem.roundOrder = values.recruitmentRounds?.length ? values.recruitmentRounds?.length + 1 : 1;
        push(newItem);
    }

    function removeRow(index) {
        setFieldValue(
            "recruitmentRounds",
            values.recruitmentRounds
                .filter((item, i) => i !== index)
                .map((item, i) => ({
                    ...item,
                    roundOrder: i + 1, // nếu bạn muốn đánh số bắt đầu từ 1
                }))
        );
    }

    const handleOpenCandidateList = () => {
        setOpenCandidateList(true);
    };
    const handleOpen = () => {
        setOpen(true);
    };
    const handleClose = () => {
        setOpen(false);
        setOpenCandidateList(false);
    };

    const {recruitmentPlanStore} = useStore();
    const {openChooseMultipleCandidatesPopup} = recruitmentPlanStore;
    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <FieldArray name='recruitmentRounds'>
                    {({push}) => (
                        <>
                            <Grid item xs={12}>
                                <Grid container spacing={1} className=''>
                                    <Grid item xs={12} sm={4} md={2} lg={2} className="mb-12">

                                        <ButtonGroup color='container' aria-label='outlined primary button group'>
                                            <Button onClick={() => handleAddNewRow(push)} fullWidth>
                                                <Add color='white' className='mr-2 addIcon'/>
                                                Thêm vòng
                                            </Button>
                                        </ButtonGroup>
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Grid item xs={12}>
                                <Table className={classes.table} size="small">
                                    <TableHead>
                                        <TableRow sx={{backgroundColor: "#f5f5f5"}}>
                                            <TableCell width={'7%'} align="center">STT</TableCell>
                                            <TableCell width={'9%'}>Thao tác</TableCell>
                                            <TableCell>Tên vòng</TableCell>
                                            <TableCell width="20%">Ngày diễn ra</TableCell>
                                            <TableCell width="20%" className={"text-center"}>Địa điểm tổ
                                                chức</TableCell>
                                            <TableCell width="15%" className={"text-center"}>Hình thức</TableCell>
                                            <TableCell width="15%" className={"text-center"}>Mẫu đánh giá ứng
                                                viên</TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {values?.recruitmentRounds?.length > 0 ? (
                                            values?.recruitmentRounds?.map((order, index) => (
                                                <SupplyOrderRow
                                                    key={index}
                                                    handleOpen={handleOpen}
                                                    index={index}
                                                    order={order}
                                                    setChoose={setChoose}
                                                    recruitmentRounds={values?.recruitmentRounds}
                                                    nameSpace={`recruitmentRounds[${index}]`}
                                                    remove={() => removeRow(index)}
                                                    push={() => push(index)}
                                                    handleOpenCandidateList={handleOpenCandidateList}
                                                />
                                            ))
                                        ) : (
                                            <TableRow align='center' className='py-8'>
                                                <TableCell colSpan={10} className={'text-center'}>
                                                    Chưa có vòng tuyển dụng nào
                                                </TableCell>
                                            </TableRow>
                                        )}
                                    </TableBody>
                                </Table>
                            </Grid>
                            <RecruitmentPlanPersonParticipate open={open} handleClose={handleClose} value={choose}/>
                            {openCandidateList && (
                                <CandidateRoundListPopup
                                    open={openCandidateList}
                                    handleClose={handleClose}
                                    value={choose}
                                />
                            )}
                            {openChooseMultipleCandidatesPopup && <ChooseMultipleCandidatesPopup/>}
                        </>
                    )}
                </FieldArray>
            </Grid>
        </Grid>
    );
};
const SupplyOrderRow = memo(({index, remove, nameSpace, disabled}) => {
    const classes = useStyles();
    const {setFieldValue, values} = useFormikContext();
    const withNameSpace = useCallback((field) => field ? `${nameSpace}.${field}` : nameSpace, [nameSpace]);
    const currentOpen = useMemo(() => getIn(values, withNameSpace("open")) ?? false, [values, withNameSpace]);

    const handleToggleOpen = useCallback(() => {
        setFieldValue(withNameSpace("open"), !currentOpen);
    }, [currentOpen, setFieldValue, withNameSpace]);

    return (
        <>
            <TableRow className='row-table-body' key={index}>
                <TableCell>
                    <GlobitsNumberInput disabled name={withNameSpace("roundOrder")}/>
                </TableCell>
                {!disabled && (
                    <TableCell align='center'>
                        <span className='pointer tooltip text-red' style={{cursor: "pointer"}} onClick={remove}>
                            <Delete className='text-red'/>
                        </span>
                        <IconButton
                            onClick={handleToggleOpen}
                            color="primary"
                            size="small"
                        >
                            {currentOpen ? <ExpandLessIcon/> : <ExpandMoreIcon/>}
                        </IconButton>
                    </TableCell>
                )}
                <TableCell>
                    <GlobitsTextField name={withNameSpace("name")}/>
                </TableCell>
                <TableCell>
                    <GlobitsDateTimePicker name={withNameSpace("takePlaceDate")}/>
                </TableCell>
                <TableCell>
                    <GlobitsPagingAutocompleteV2 name={withNameSpace("interviewLocation")} api={pagingWorkplace}/>
                </TableCell>
                <TableCell>
                    <GlobitsSelectInputV2 name={withNameSpace("recruitmentType")} options={RECRUITMENT_TYPE}/>
                </TableCell>
                <TableCell>
                    <GlobitsPagingAutocomplete
                        name={withNameSpace("evaluationTemplate")}
                        customData={"data"}
                        displayData='name'
                        api={paging}
                        keyValue='value'
                    />
                </TableCell>
            </TableRow>
            <TableRow>
                <TableCell colSpan={7} style={{padding: 0}}>
                    <Collapse in={currentOpen} timeout="auto" unmountOnExit>
                        <Box sx={{margin: 10}}>
                            <ParticipatingPeopleTable nameSpace={nameSpace} classes={classes}/>
                        </Box>
                        <Box sx={{margin: 10}}>
                            <CandidatesTable nameSpace={nameSpace} classes={classes} roundId={values?.id}
                                             title={getIn(values, withNameSpace("name"))}/>
                        </Box>
                    </Collapse>
                </TableCell>
            </TableRow>
        </>
    );
});
const ParticipatingPeopleTable = memo(({nameSpace, classes}) => {
    const {setFieldValue, values} = useFormikContext();
    const withNameSpace = useCallback(
        (field) => (nameSpace ? `${nameSpace}.${field}` : field),
        [nameSpace]
    );
    const participatingPeople = useMemo(() => getIn(values, withNameSpace("participatingPeople")) || [], [values, withNameSpace]);

    const handleAddParticipant = useCallback((value, push) => {
        if (value) {
            if (participatingPeople.some(item => item.id === value?.id)) {
                toast.warning(`Đã tồn tại ${value?.displayName} trong vòng tuyển dụng này`);
            } else {
                push(value);
                setFieldValue(withNameSpace("replacedPerson"), null);
            }
        }
    }, [participatingPeople, setFieldValue, withNameSpace]);

    const handleJudgePersonChange = useCallback((subIndex, checked) => {
        const path = `${withNameSpace("participatingPeople")}[${subIndex}].judgePerson`;
        const currentIdPath = `${withNameSpace("participatingPeople")}[${subIndex}].id`;
        const currentId = getIn(values, currentIdPath);

        if (checked.target.checked) {
            const hasOtherJudge = participatingPeople.some(
                (item) => item?.judgePerson === true && item?.id !== currentId
            );

            if (hasOtherJudge) {
                toast.warning("Chỉ có 1 người đưa ra quyết định");
            } else {
                setFieldValue(path, checked.target.checked);
            }
        } else {
            setFieldValue(path, checked.target.checked);
        }
    }, [participatingPeople, setFieldValue, values, withNameSpace]);

    const renderStaffInfo = (staff) => (
        <>
            {staff.displayName && <p className='m-0'><strong>{staff.displayName}</strong></p>}
            {staff.birthDate && <p className='m-0'>Ngày sinh: {formatDate("DD/MM/YYYY", staff.birthDate)}</p>}
            {staff.gender && (
                <p className='m-0'>
                    Giới tính: {staff.gender === "M" ? "Nam" : staff.gender === "F" ? "Nữ" : ""}
                </p>
            )}
            {staff.birthPlace && <p className='m-0'>Nơi sinh: {staff.birthPlace}</p>}
        </>
    );

    return (
        <FieldArray name={`${withNameSpace("participatingPeople")}`}>
            {({push, remove}) => (
                <>
                    <Grid container spacing={2} className='mb-2' style={{padding: 0}}>
                        <Grid style={{alignItems: 'center', display: 'flex'}} item>
                            <Typography variant="subtitle1">
                                Nhân sự tham gia
                            </Typography>
                        </Grid>
                        <Grid item sm={3} xs={12} md={3}>
                            <ChooseUsingStaffSection
                                handleAfterSubmit={(value) => handleAddParticipant(value, push)}
                                name={withNameSpace('replacedPerson')}
                                placeholder='Chọn nhân viên tham gia'
                            />
                        </Grid>
                    </Grid>
                    <Table className={classes.table} size="small" sx={{border: 1}}>
                        <TableHead>
                            <TableRow>
                                <TableCell>Thao tác</TableCell>
                                <TableCell>Người quyết định</TableCell>
                                <TableCell>Mã nhân viên </TableCell>
                                <TableCell>Nhân viên</TableCell>
                                <TableCell>Thông tin liên hệ</TableCell>
                                <TableCell>Phòng ban</TableCell>
                                <TableCell>Chức danh</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {participatingPeople.map((staff, subIndex) => (
                                <TableRow key={`staff-${subIndex}`}>
                                    <TableCell align='center'>
                                    <span className='pointer tooltip text-red'
                                          style={{cursor: "pointer"}} onClick={() => remove(subIndex)}>
                                    <Delete className='text-red'/>
                                    </span>
                                    </TableCell>
                                    <TableCell>
                                        <GlobitsCheckBox
                                            name={`${withNameSpace("participatingPeople")}[${subIndex}].judgePerson`}
                                            className="p-6"
                                            handleChange={(checked) => handleJudgePersonChange(subIndex, checked)}
                                        />
                                    </TableCell>
                                    <TableCell>{staff?.staffCode}</TableCell>
                                    <TableCell>{renderStaffInfo(staff)}</TableCell>
                                    <TableCell>
                                        <Typography>SĐT:{staff?.email}</Typography>
                                        <Typography>EMAIL:{staff?.phoneNumber}</Typography>
                                    </TableCell>
                                    <TableCell>{staff?.department?.name}</TableCell>
                                    <TableCell>{staff?.currentPosition?.name}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </>
            )}
        </FieldArray>
    );
});
const CandidatesTable = observer(({nameSpace, classes, roundId, title}) => {
    const {setFieldValue, values} = useFormikContext();
    const {passListToNextRound, handleOpenCreateEdit, openCreateEditPopup} = useStore().candidateRecruitmentRoundStore;
    const {getById, saveRecruitmentPlan} = useStore().recruitmentPlanStore
    const withNameSpace = useCallback(
        (field) => (nameSpace ? `${nameSpace}.${field}` : field),
        [nameSpace]
    );
    const candidates = useMemo(() => getIn(values, withNameSpace("candidates")) || [], [values, withNameSpace]);
    const passToNextRound = (async (value) => {
        await saveRecruitmentPlan(values)
        await passListToNextRound(value);
        await getById(values.id)
    });
    const handleToggleAllCandidates = useCallback((e) => {
        const isChecked = e.target.checked;
        const updated = candidates.map(c => ({...c, checked: isChecked}));
        setFieldValue(withNameSpace("candidates"), updated);
    }, [candidates, setFieldValue, withNameSpace]);

    const handleToggleCandidate = useCallback((subIndex, checked) => {
        setFieldValue(`${withNameSpace("candidates")}[${subIndex}].checked`, checked);
    }, [setFieldValue, withNameSpace]);

    const renderCandidateInfo = (candidate) => (
        <>
            {candidate?.displayName && <p className='m-0'><strong>{candidate.displayName}</strong></p>}
            {candidate?.birthDate && (
                <p className='m-0'>Ngày sinh: {formatDate("DD/MM/YYYY", candidate.birthDate)}</p>
            )}
            {candidate?.gender && (
                <p className='m-0'>
                    Giới tính: {candidate.gender === "M" ? "Nam" : candidate.gender === "F" ? "Nữ" : ""}
                </p>
            )}
            {candidate?.birthPlace && <p className='m-0'>Nơi sinh: {candidate.birthPlace}</p>}
        </>
    );
    const checkedCandidates = useMemo(() => candidates.filter(value => value?.checked), [candidates]);
    const checkedCount = checkedCandidates.length;
    const allChecked = candidates.length > 0 && candidates.every(c => c.checked);
    const someChecked = candidates.some(c => c.checked) && !allChecked;
    const [anchorEl, setAnchorEl] = useState(null);
    const open = Boolean(anchorEl);

    const handleClick = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };
    return (
        <FieldArray name={`${withNameSpace("candidates")}`}>
            {({push, remove, replace}) => (
                <>
                    <Grid container spacing={2} className='mb-2' style={{gap: 2}}>
                        <Grid style={{alignItems: 'center', display: 'flex'}} item>
                            <Typography variant="subtitle1">
                                Hồ sơ ứng tuyển
                            </Typography>
                        </Grid>
                        {/*{getIn(values, "id") && (*/}
                        {/*    <Grid item>*/}
                        {/*        <ButtonGroup color='container' aria-label='outlined primary button group'>*/}
                        {/*            <Button fullWidth onClick={() => handleOpenCreateEdit()}>*/}
                        {/*                <AddIcon color='white' className='mr-2'/>*/}
                        {/*                Thêm mới*/}
                        {/*            </Button>*/}
                        {/*        </ButtonGroup>*/}
                        {/*    </Grid>*/}
                        {/*)}*/}
                        {roundId && (
                            <>
                                <Grid item>
                                    <ButtonGroup color='container' aria-label='outlined primary button group'>
                                        <Button
                                            disabled={checkedCount === 0}
                                            fullWidth
                                            onClick={() => passToNextRound(checkedCandidates.map(value => value.id))}
                                        >
                                            <RoundedCorner color='white' className='mr-2'/>
                                            Chuyển sang vòng sau
                                        </Button>
                                    </ButtonGroup>
                                </Grid>
                                <Grid item>
                                    <ButtonGroup color='container' aria-label='outlined primary button group'>
                                        <Button
                                            disabled={checkedCount !== 1}
                                            startIcon={<AddIcon fontSize='small'/>}
                                            onClick={async () => {
                                                await doActionAssignment(checkedCandidates[0]?.id, CandidateStatusEnum.PENDING_ASSIGNMENT);
                                            }}
                                        >
                                            Xác nhận tuyển
                                        </Button>
                                    </ButtonGroup>
                                </Grid>
                            </>
                        )}
                    </Grid>
                    <Table className={classes.table} size="small" sx={{border: 1}}>
                        <TableHead>
                            <TableRow>
                                <TableCell padding="checkbox">
                                    <Checkbox
                                        checked={allChecked}
                                        indeterminate={someChecked}
                                        onChange={handleToggleAllCandidates}
                                    />
                                </TableCell>
                                <TableCell>Thao tác</TableCell>
                                <TableCell className={classes.fitWidthCell}>Email</TableCell>
                                <TableCell>Tên</TableCell>
                                <TableCell>Thông tin liên hệ</TableCell>
                                <TableCell>Vị trí tuyển dụng</TableCell>
                                <TableCell>Ngày phỏng vấn</TableCell>
                                <TableCell>Trạng thái vòng phỏng vấn</TableCell>
                                <TableCell>Kết quả vòng phỏng vấn</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {candidates.map((candidate, subIndex) => (
                                <TableRow key={`candidate-${subIndex}`}>
                                    <TableCell padding="checkbox">
                                        <Checkbox
                                            checked={!!candidate.checked}
                                            onChange={(e) => handleToggleCandidate(subIndex, e.target.checked)}
                                        />
                                    </TableCell>
                                    <TableCell align='center'>
                                        <IconButton onClick={handleClick}>
                                            <MoreVertIcon/>
                                        </IconButton>
                                        <Menu
                                            anchorEl={anchorEl}
                                            open={open}
                                            onClose={handleClose}
                                            anchorOrigin={{vertical: 'bottom', horizontal: 'right'}}
                                            transformOrigin={{vertical: 'top', horizontal: 'right'}}
                                        >
                                            <MenuItem onClick={() => {
                                                handleOpenCreateEdit(candidate);
                                                handleClose();
                                            }}>
                                                <EditIcon className="text-primary" fontSize="small"
                                                          style={{marginRight: 8}}/>
                                                Chỉnh sửa
                                            </MenuItem>
                                            <MenuItem onClick={() => {
                                                remove(subIndex);
                                                handleClose();
                                            }}>
                                                <DeleteIcon className="text-red" fontSize="small"
                                                            style={{marginRight: 8}}/>
                                                Xóa
                                            </MenuItem>
                                        </Menu>
                                    </TableCell>
                                    <TableCell
                                        className={classes.fitWidthCell}>{candidate?.candidate?.email}</TableCell>
                                    <TableCell>{renderCandidateInfo(candidate?.candidate)}</TableCell>
                                    <TableCell>{candidate?.candidate?.phoneNumber}</TableCell>
                                    <TableCell>{candidate?.candidate?.positionTitle?.name}</TableCell>
                                    <TableCell>
                                        <GlobitsDateTimePicker
                                            name={`${withNameSpace(`candidates[${subIndex}].actualTakePlaceDate`)}`}
                                            validate={true}
                                            format="dd/MM/yyyy HH:mm"
                                            isDateTimePicker={true}
                                        />
                                    </TableCell>
                                    <TableCell>
                                        <GlobitsSelectInputV2
                                            name={`${withNameSpace(`candidates[${subIndex}].recruitmentType`)}`}
                                            options={RECRUITMENT_TYPE}
                                        />
                                    </TableCell>
                                    <TableCell>
                                        <GlobitsSelectInputV2
                                            name={`${withNameSpace(`candidates[${subIndex}].resultStatus`)}`}
                                            options={RESULT_STATUS}
                                        />
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                    {openCreateEditPopup && (
                        <CandidateRecruitmentRoundPopup title={title} handleAfterSubmit={(value) => {
                            const isExist = candidates.some(c => c?.recruitmentRound?.id === value?.recruitmentRound?.id && c.candidate.id === value.candidate.id);
                            if (isExist) {
                                const index = candidates.findIndex(c => c.candidate.id === value.candidate.id);
                                replace(index, value);
                            } else {
                                push(value)
                            }
                        }}/>
                    )}
                </>
            )}
        </FieldArray>
    );
});
export default memo(observer(TabRecruitmentRound));
