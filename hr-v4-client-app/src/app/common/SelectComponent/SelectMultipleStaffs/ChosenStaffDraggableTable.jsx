import React, { memo, useMemo, useState } from "react";
import {
    closestCenter,
    DndContext,
    DragOverlay,
    KeyboardSensor,
    MouseSensor,
    TouchSensor,
    useSensor,
    useSensors
} from "@dnd-kit/core";
import { restrictToVerticalAxis } from "@dnd-kit/modifiers";
import {
    arrayMove,
    SortableContext,
    verticalListSortingStrategy
} from "@dnd-kit/sortable";
import { observer } from "mobx-react";
import {
    Button,
    ButtonGroup,
    Grid,
    makeStyles,
    Tooltip
} from "@material-ui/core";
import { FieldArray, useFormikContext } from "formik";
import { useTranslation } from "react-i18next";
import AddIcon from "@material-ui/icons/Add";
import DraggableStaffRow from "app/views/Salary/SalaryResultBoardConfig/SalaryResultConfigTabs/ResultStaffsTable/DraggableStaffRow";
import StaticStaffRow from "app/views/Salary/SalaryResultBoardConfig/SalaryResultConfigTabs/ResultStaffsTable/StaticStaffRow";

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
        // overflowY: "hidden",
        "& table": {
            border: "1px solid #ccc",
            borderCollapse: "collapse",
            "& td": {
                border: "1px solid #ccc",
            }
        }
    },
    tableHeader: {
        // width: "100%",
        borderBottom: "1px solid #ccc",
        marginBottom: "8px",
        "& th": {
            border: "1px solid #ccc",
            padding: "8px 0 8px 4px"
        },
    },
}));


function ChosenStaffDraggableTable(props) {
    const {
        isDraggableTable = false,
    } = props;


    const classes = useStyles();
    const { values, setFieldValue } = useFormikContext();

    const [activeId, setActiveId] = useState();
    const allRowIds = useMemo(() => values?.staffs?.map(({ id }) => id), [values?.staffs, values?.staffs?.length]);

    const sensors = useSensors(
        useSensor(MouseSensor, {}),
        useSensor(TouchSensor, {}),
        useSensor(KeyboardSensor, {})
    );

    function handleDragStart(event) {
        setActiveId(event.active.id);
    }

    function handleDragEnd(event) {
        const { active, over } = event;

        if (active.id !== over.id) {
            const oldIndex = allRowIds.indexOf(active?.id);
            const newIndex = allRowIds.indexOf(over?.id);

            // Update displayOrder and rearrange items in one step
            const newData = arrayMove(values?.staffs, oldIndex, newIndex).map((item, index) => ({
                ...item,
                displayOrder: index + 1,
            }));

            setFieldValue("staffs", newData);
        }

        setActiveId(null);
    }

    function handleDragCancel() {
        setActiveId(null);
    }

    const selectedRow = useMemo(() => {
        if (!activeId) {
            return null;
        }
        const row = values?.staffs?.find((item) => item.id === activeId);
        return row;
    }, [activeId]);

    // console.log("chekcing values?.staffs: ", values?.staffs);


    return (
        <FieldArray name="staffs">
            {({ insert, remove, push }) => (
                <section className={classes.tableContainer}>
                    <table className={`${classes.table} w-100`} style={{ tableLayout: "auto" }}>
                        <thead>
                            <tr className={classes.tableHeader}>
                                <th width="128px" align="center"> <span className="px-6">Thao tác</span></th>
                                <th width="15%">Mã NV</th>
                                <th width="30%">Tên nhân viên</th>
                                <th width="20%">Ngày sinh</th>
                                <th width="20%">Giới tính</th>
                            </tr>
                        </thead>

                        <tbody>
                            <DndContext
                                sensors={sensors}
                                onDragEnd={handleDragEnd}
                                onDragStart={handleDragStart}
                                onDragCancel={handleDragCancel}
                                collisionDetection={closestCenter}
                                modifiers={[restrictToVerticalAxis]}
                            >
                                <SortableContext items={allRowIds} strategy={verticalListSortingStrategy}>
                                    {values?.staffs?.length > 0 ? (
                                        values?.staffs?.map(function (item, index) {
                                            return (
                                                <DraggableStaffRow
                                                    key={item?.id}
                                                    data={item}
                                                    index={index}
                                                    nameSpace={`staffs[${index}]`}
                                                    remove={() => remove(index)}
                                                />
                                            )
                                        })
                                    ) : (
                                        <tr className='row-table-body row-table-no_data'>
                                            <td colSpan={5} align='center' className="py-8">Chưa chọn nhân viên</td>
                                        </tr>
                                    )}
                                </SortableContext>

                                <DragOverlay>
                                    {activeId && (
                                        <table className={`${classes.table} w-100`}>
                                            <tbody>
                                                <StaticStaffRow
                                                    data={selectedRow}
                                                    key={selectedRow?.id}
                                                />
                                            </tbody>
                                        </table>
                                    )}
                                </DragOverlay>
                            </DndContext>
                        </tbody>
                    </table>
                </section>
            )}
        </FieldArray>
    );
}

export default memo(observer(ChosenStaffDraggableTable));