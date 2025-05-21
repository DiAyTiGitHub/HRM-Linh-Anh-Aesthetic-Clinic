import {Button, ButtonGroup, Grid, Tooltip} from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import {useStore} from "app/stores";
import {memo} from "react";

import {observer} from "mobx-react";
import {CandidateStatusEnum} from "../../../../LocalConstants";

const CandidateRoundListPopupIndexToolbar = ({handleByIdRecruitment}) => {
    const {
        handleOpenCreateEdit,
        doActionAssignment,
        listOnDelete,
        passToNextRound,
        rejectCandidateRound,
        resetStore
    } = useStore().candidateRecruitmentRoundStore;
    return (
        <Grid container spacing={2} className='p-4'>
            <Grid item>
                <ButtonGroup color='container' aria-label='outlined primary button group'>
                    <Tooltip arrow placement='top' title='Xác nhận tham gia và Tạo lịch phỏng vấn'>
                        <Button startIcon={<AddIcon fontSize='small'/>} onClick={() => handleOpenCreateEdit()}>
                            Thêm mới
                        </Button>
                    </Tooltip>
                </ButtonGroup>
            </Grid>
            <Grid item>
                <ButtonGroup color='container' aria-label='outlined primary button group'>
                    <Tooltip arrow placement='top' title='Xác nhận tuyển'>
                        <Button disabled={listOnDelete?.length !== 1} startIcon={<AddIcon fontSize='small'/>}
                                onClick={async () => {
                                    await doActionAssignment(listOnDelete[0]?.id, CandidateStatusEnum.PENDING_ASSIGNMENT)
                                    resetStore()
                                    handleByIdRecruitment()
                                }}>
                            Xác nhận tuyển
                        </Button>
                    </Tooltip>
                </ButtonGroup>
            </Grid>
            <Grid item>
                <ButtonGroup color='container' aria-label='outlined primary button group'>
                    <Tooltip arrow placement='top' title='Chuyển sang vòng tiếp theo'>
                        <Button disabled={listOnDelete?.length !== 1} startIcon={<AddIcon fontSize='small'/>}
                                onClick={async () => {
                                    await passToNextRound(listOnDelete[0]?.id)
                                    resetStore()
                                    handleByIdRecruitment()
                                }}>
                            Chuyển sang vòng tiếp theo
                        </Button>
                    </Tooltip>
                </ButtonGroup>
            </Grid>
            <Grid item>
                <ButtonGroup color='container' aria-label='outlined primary button group'>
                    <Tooltip arrow placement='top' title='Đánh trượt'>
                        <Button disabled={listOnDelete?.length !== 1} startIcon={<AddIcon fontSize='small'/>}
                                onClick={async () => {
                                    await rejectCandidateRound(listOnDelete[0]?.id)
                                    resetStore()
                                    handleByIdRecruitment()
                                }}>
                            Đánh trượt
                        </Button>
                    </Tooltip>
                </ButtonGroup>
            </Grid>
        </Grid>
    );
};
export default memo(observer(CandidateRoundListPopupIndexToolbar));
