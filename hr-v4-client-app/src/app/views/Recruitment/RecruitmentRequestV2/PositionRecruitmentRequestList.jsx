import { Button, ButtonGroup, Grid, Icon, IconButton, makeStyles, Tooltip } from "@material-ui/core";
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import DeleteIcon from "@material-ui/icons/Delete";
import TouchAppIcon from "@material-ui/icons/TouchApp";
import { t } from "app/common/CommonFunctions";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import SelectMulPositionsPopup from "app/views/Position/SelectMultiplePositions/SelectMulPositionsPopup";

const useStyles = makeStyles(() => ({
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
        marginTop: "2px",
        overflowX: "auto",
        "& table": {
            border: "1px solid #ccc",
            borderCollapse: "collapse",
            "& td": {
                border: "1px solid #ccc",
            },
        },
    },
    tableHeader: {
        borderBottom: "1px solid #ccc",
        marginBottom: "8px",
        "& th": {
            border: "1px solid #ccc",
            padding: "8px 0 8px 4px",
        },
    },
}));

function PositionRecruitmentRequestList(props) {
    const classes = useStyles();
    const { values, setFieldValue } = useFormikContext();

    const { handleOpenSelectMultiplePopup, openSelectMultiplePopup, currentPositions, handleClose } =
        useStore().positionStore;

    const handleConfirmPosition = (listPosition) => {
        const oldRequests = values.positionRequests || [];

        const dto = listPosition.map((pos) => {
            const oldItem = oldRequests.find((req) => req.position?.id === pos.id);
            return {
                position: pos,
                previousStaffId: oldItem?.previousStaffId || pos?.previousStaff?.id,
                previousStaffDisplayName: oldItem?.previousStaffDisplayName || pos?.previousStaff?.displayName || "",
            };
        });

        setFieldValue("positionRequests", dto);
        handleClose();
    };

    const columns = [
        {
            title: t("general.action"),
            width: "10%",
            render: (rowData, index) => {
                return (
                    <div>
                        <Tooltip placement='top' title='Xóa'>
                            <IconButton
                                size='small'
                                onClick={() => {
                                    const newPositionRequests = [...values.positionRequests];
                                    newPositionRequests.splice(index, 1);
                                    setFieldValue("positionRequests", newPositionRequests);
                                }}>
                                <DeleteIcon fontSize='small' color='error' />
                            </IconButton>
                        </Tooltip>
                    </div>
                );
            },
            align: "center",
        },
        {
            title: "Mã vị trí",
            field: "code",
            width: "10%",
            render: (row) => <span>{row?.position?.code}</span>,
            align: "center",
        },
        {
            title: "Tên vị trí",
            field: "title",
            width: "20%",
            render: (row) => <span>{row?.position?.name}</span>,
            align: "center",
        },
        {
            title: "Nhân viên hiện tại",
            field: "title",
            width: "20%",
            render: (row) => <span className='px-4'>{`${row?.position?.staff?.displayName}`}</span>,
            align: "center",
        },
        {
            title: "Nhân viên tiền nhiệm",
            field: "previousStaffDisplayName", // hoặc để trống nếu chỉ dùng render
            width: "20%",
            render: (row) => {
                const displayName = row?.previousStaffDisplayName ?? row?.position?.previousStaff?.displayName ?? "";
                return <span className='px-4'>{displayName}</span>;
            },
            align: "center",
        },
    ];

    return (
        <>
            <Grid item xs={12} md={6}>
                <ButtonGroup color='container' aria-label='outlined primary button group'>
                    <Button
                        startIcon={<TouchAppIcon />}
                        onClick={() => {
                            const listPosition = values?.positionRequests?.map((i) => i.position);
                            handleOpenSelectMultiplePopup(listPosition);
                        }}>
                        Chọn vị thay thế
                    </Button>
                </ButtonGroup>
            </Grid>
            <FieldArray name='positionRequests'>
                {({ insert, remove, push }) => (
                    <section className={classes.tableContainer}>
                        <Grid item xs={12}>
                            <GlobitsTable
                                data={values?.positionRequests || []}
                                columns={columns}
                                nonePagination
                                selection={false}
                            />
                        </Grid>
                    </section>
                )}
            </FieldArray>
            {openSelectMultiplePopup && (
                <SelectMulPositionsPopup handleSubmit={handleConfirmPosition} searchObject={{ oldPosition: true }} />
            )}
        </>
    );
}

export default memo(observer(PositionRecruitmentRequestList));
