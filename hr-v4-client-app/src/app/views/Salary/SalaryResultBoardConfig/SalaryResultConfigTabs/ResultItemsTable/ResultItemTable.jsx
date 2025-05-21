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
import ResultItemDraggableRow from "./ResultItemDraggableRow";
import ResultItemStaticRow from "./ResultItemStaticRow";
import { useTranslation } from "react-i18next";
import AddIcon from "@material-ui/icons/Add";
import { SalaryResultItem } from "app/common/Model/Salary/SalaryResultItem";
import TouchAppIcon from '@material-ui/icons/TouchApp';
import TemplateItemCUPopup from "app/views/Salary/SalaryTemplateCU/SalaryTemplateCUPopup/TemplateItemCUPopup";
import ResultItemChooseMultiple from "./ResultItemChooseMultiple";
import { useStore } from "app/stores";

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

function ResultItemTable() {
    const { t } = useTranslation();
    const classes = useStyles();
    const { values, setFieldValue } = useFormikContext();


    // code handle for drag and drop row in table
    const [activeId, setActiveId] = useState();
    const allRowIds = useMemo(() => values?.resultItems?.map(({ id }) => id), [values?.resultItems, values?.resultItems?.length]);

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
            const newData = arrayMove(values?.resultItems, oldIndex, newIndex).map((item, index) => ({
                ...item,
                displayOrder: index + 1,
            }));

            setFieldValue("resultItems", newData);
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
        const row = values?.resultItems?.find((item) => item.id === activeId);
        return row;
    }, [activeId]);



    // handle open choose salary items used in SalaryResult
    const [openChooseItems, setOpenChooseItems] = useState(null);

    function handleOpenChooseItems() {
        setOpenChooseItems(true);
    }

    function handleCloseChooseItems() {
        setOpenChooseItems(false);
    }

    // Render the UI for your table
    return (
        <>
            <Grid container spacing={2}>
                <FieldArray name="resultItems">
                    {({ insert, remove, push }) => (
                        <>
                            <Grid item xs={12}>
                                <Grid container spacing={2}>
                                    <Grid item xs={12} sm={6} md={4}>
                                        <ButtonGroup
                                            color="container"
                                            aria-label="outlined primary button group"
                                        >
                                            <Button
                                                startIcon={<TouchAppIcon />}
                                                type="button"
                                                onClick={() => handleOpenChooseItems()}
                                            >
                                                Chọn thành phần lương
                                            </Button>
                                        </ButtonGroup>
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Grid item xs={12} style={{ overflowX: "auto" }}>
                                <section className={`${classes.tableContainer} commonTableContainer`}>
                                    <table className={`${classes.table} w-100`} style={{ tableLayout: "auto" }}>
                                        <thead>
                                            <tr className={classes.tableHeader}>
                                                <th width="128px" className="stickyCell"> <span className="px-6">Thao tác</span></th>
                                                <th className="">Tên thành phần</th>
                                                <th className="">Mã/tham số TP</th>
                                                <th>Tính chất TP</th>
                                                <th>Cách tính</th>
                                                <th style={{ minWidth: "280px" }}>Tên cột hiển thị</th>
                                                <th style={{ minWidth: "240px" }}>Nhóm cột</th>
                                                <th style={{ minWidth: "320px" }}>Công thức/Giá trị/Đầu vào tính ngưỡng</th>
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
                                                    {values?.resultItems?.length > 0 ? (
                                                        values?.resultItems?.map(function (item, index) {
                                                            return (
                                                                <ResultItemDraggableRow
                                                                    key={item?.id}
                                                                    data={item}
                                                                    index={index}
                                                                    nameSpace={`resultItems[${index}]`}
                                                                    remove={() => remove(index)}
                                                                    // handleOpenCUResultItem={handleOpenCUResultItem}
                                                                />
                                                            )
                                                        })
                                                    ) : (
                                                        <tr className='row-table-body row-table-no_data'>
                                                            <td colSpan={8} align='center' className="py-8">Chưa chọn thành phần lương</td>
                                                        </tr>
                                                    )}
                                                </SortableContext>

                                                <DragOverlay>
                                                    {activeId && (
                                                        <table className={`${classes.table} w-100`}>
                                                            <tbody>
                                                                <ResultItemStaticRow
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
                            </Grid>

                        </>
                    )}
                </FieldArray>
            </Grid>

            {openChooseItems && (
                <ResultItemChooseMultiple
                    isOpen={openChooseItems}
                    handleClosePopup={handleCloseChooseItems}
                />
            )}
        </>
    );
}


export default memo(observer(ResultItemTable));