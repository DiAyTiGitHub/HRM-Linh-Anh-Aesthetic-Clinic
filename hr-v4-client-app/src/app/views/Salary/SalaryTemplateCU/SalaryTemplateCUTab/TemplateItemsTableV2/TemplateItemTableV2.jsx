import React , { memo , useEffect , useMemo , useState } from "react";
import {
    closestCenter ,
    DndContext ,
    DragOverlay ,
    KeyboardSensor ,
    MouseSensor ,
    TouchSensor ,
    useSensor ,
    useSensors
} from "@dnd-kit/core";
import { restrictToVerticalAxis } from "@dnd-kit/modifiers";
import { arrayMove , SortableContext , verticalListSortingStrategy } from "@dnd-kit/sortable";
import { observer } from "mobx-react";
import { Button , ButtonGroup , Grid , makeStyles } from "@material-ui/core";
import { FieldArray , useFormikContext } from "formik";
import { SalaryTemplateItem } from "app/common/Model/Salary/SalaryTemplateItem";
import TemplateItemCUPopup from "../../SalaryTemplateCUPopup/TemplateItemCUPopup";
import ItemDraggableRow from "./ItemDraggableRow";
import ItemStaticRow from "./ItemStaticRow";
import { useTranslation } from "react-i18next";
import TouchAppIcon from '@material-ui/icons/TouchApp';
import TemplateItemChooseMultiple
    from "../../SalaryTemplateCUPopup/SalaryTemplateChooseMultipleItems/TemplateItemChooseMultiple";
import { useStore } from "app/stores";

const useStyles = makeStyles(() => ({
    root:{
        background:"#E4f5fc" ,
        padding:"10px 15px" ,
        borderRadius:"5px" ,
    } ,
    groupContainer:{
        width:"100%" ,
        "& .MuiOutlinedInput-root":{
            borderRadius:"0!important" ,
        } ,
    } ,
    tableContainer:{
        marginTop:"2px" ,
        overflowX:"auto" ,
        // overflowY: "hidden",
        "& table":{
            border:"1px solid #ccc" ,
            borderCollapse:"collapse" ,
            "& td":{
                border:"1px solid #ccc" ,
            }
        }
    } ,
    tableHeader:{
        // width: "100%",
        borderBottom:"1px solid #ccc" ,
        marginBottom:"8px" ,
        "& th":{
            border:"1px solid #ccc" ,
            padding:"8px 0 8px 4px"
        } ,
    } ,
}));

function TemplateItemTableV2() {
    const {t} = useTranslation();
    const classes = useStyles();
    const {values , setFieldValue} = useFormikContext();

    const [selectedTemplateItem , setSeletedTemplateItem] = useState(null);
    const {salaryTemplateStore , hrRoleUtilsStore} = useStore();

    const {selectedSalaryTemplate , openViewPopup:readOnly} = salaryTemplateStore;
    const {
        isAdmin ,
        isManager ,
        isStaffView
    } = hrRoleUtilsStore;
    const [initialDataInputFormula , setInitialDataInputFormula] = useState([]);

    useEffect(() => {
        if (selectedSalaryTemplate?.templateItems?.length > 0) {
            const salaryItemCodes = selectedSalaryTemplate.templateItems.map((item) => item.salaryItem?.code)
            setInitialDataInputFormula(salaryItemCodes)
        }
    } , [selectedSalaryTemplate]);

    function handleOpenCUTemplateItem(templateItem) {
        if (templateItem?.id) {
            setSeletedTemplateItem(templateItem);
            return;
        }

        const newRow = new SalaryTemplateItem();
        newRow.salaryTemplateId = values?.id;
        newRow.isNew = true;

        if (values?.templateItems?.length) {
            newRow.displayOrder = values?.templateItems?.length + 1;
        } else {
            newRow.displayOrder = 1;
        }

        setSeletedTemplateItem(newRow);
    }

    function handleConfirmCUTemplateItem(cuItem) {
        try {
            if (cuItem?.isNew) {
                cuItem.isNew = false;
            }

            const currentData = values?.templateItems || [];
            const index = currentData.findIndex(item => item.id === cuItem.id);

            if (index !== -1) {
                // Replace the existing item
                currentData[index] = cuItem;
            } else {
                // Add the new item if no duplicate
                currentData.push(cuItem);
            }

            setFieldValue("templateItems" , currentData);

            handleCloseCUTemplateItem();
        } catch (error) {
            console.error(error);
        }
    }

    function handleCloseCUTemplateItem() {
        setSeletedTemplateItem(null);
    }

    const [activeId , setActiveId] = useState();
    const allRowIds = useMemo(() => values?.templateItems?.map(({id}) => id) , [values?.templateItems , values?.templateItems?.length]);

    const sensors = useSensors(
        useSensor(MouseSensor , {}) ,
        useSensor(TouchSensor , {}) ,
        useSensor(KeyboardSensor , {})
    );

    function handleDragStart(event) {
        setActiveId(event.active.id);
    }

    function handleDragEnd(event) {
        const {active , over} = event;

        if (active.id !== over.id) {
            const oldIndex = allRowIds.indexOf(active?.id);
            const newIndex = allRowIds.indexOf(over?.id);

            // Update displayOrder and rearrange items in one step
            const newData = arrayMove(values?.templateItems , oldIndex , newIndex).map((item , index) => ({
                ... item ,
                displayOrder:index + 1 ,
            }));

            setFieldValue("templateItems" , newData);
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
        const row = values?.templateItems?.find((item) => item.id === activeId);
        return row;
    } , [activeId]);


    // handle open choose salary items used in SalaryResult
    const [openChooseItems , setOpenChooseItems] = useState(null);

    function handleOpenChooseItems() {
        setOpenChooseItems(true);
    }

    function handleCloseChooseItems() {
        setOpenChooseItems(false);
    }


    return (
        <>
            <Grid container spacing={2}>
                <FieldArray name="templateItems">
                    {({insert , remove , push}) => (
                        <>
                            <Grid item xs={12}>
                                <Grid container spacing={2}>
                                    <Grid item xs={12} sm={6} md={4}>
                                        {((isAdmin || isManager) && !readOnly) && (
                                            <ButtonGroup
                                                color="container"
                                                aria-label="outlined primary button group"
                                            >
                                                <Button
                                                    startIcon={<TouchAppIcon/>}
                                                    type="button"
                                                    onClick={() => handleOpenChooseItems()}
                                                >
                                                    Chọn thành phần lương
                                                </Button>
                                            </ButtonGroup>
                                        )}
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Grid item xs={12} style={{overflowX:"auto"}}>
                                <section className={classes.tableContainer}>
                                    <table className={`${classes.table} w-100`} style={{tableLayout:"auto"}}>
                                        <thead>
                                        <tr className={classes.tableHeader}>
                                            {((isAdmin || isManager) && !readOnly) ? (
                                                <th width="128px"><span className="px-6">STT</span></th>
                                            ) : (
                                                <th width="128px"><span className="px-6">Thao tác</span></th>
                                            )}
                                            <th>Mã/tham số TP</th>
                                            <th style={{minWidth:"320px"}}>Công thức/Giá trị/Đầu vào tính ngưỡng</th>
                                            {/* <th>Tính chất TP</th> */}
                                            <th style={{minWidth:"170px"}}>Cách tính</th>
                                            <th style={{minWidth:"110px"}}>Kiểu giá trị</th>
                                            <th style={{minWidth:"280px"}}>Tên cột hiển thị</th>
                                            <th style={{minWidth:"280px"}}>Nhóm cột</th>
                                            <th style={{minWidth:"120px"}}>Ẩn tại bảng lương</th>
                                            <th style={{minWidth:"120px"}}>Ẩn tại phiếu lương</th>
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
                                                {values?.templateItems?.length > 0 ? (
                                                    values?.templateItems?.map(function (item , index) {
                                                        return (
                                                            <ItemDraggableRow
                                                                key={item?.id}
                                                                data={item}
                                                                index={index}
                                                                templateItemGroups={values?.templateItems}
                                                                nameSpace={`templateItems[${index}]`}
                                                                remove={() => remove(index)}
                                                                handleOpenCUTemplateItem={handleOpenCUTemplateItem}
                                                                initialDataInputFormula={initialDataInputFormula}
                                                                readOnly={readOnly}
                                                            />
                                                        )
                                                    })
                                                ) : (
                                                    <tr className='row-table-body row-table-no_data'>
                                                        <td colSpan={8} align='center' className="py-8">Chưa có thành
                                                            phần lương
                                                        </td>
                                                    </tr>
                                                )}
                                            </SortableContext>

                                            <DragOverlay>
                                                {activeId && (
                                                    <table className={`${classes.table} w-100`}>
                                                        <tbody>
                                                        <ItemStaticRow
                                                            data={selectedRow}
                                                            key={selectedRow?.id}
                                                            readOnly={readOnly}
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
                <TemplateItemChooseMultiple
                    isOpen={openChooseItems}
                    handleClosePopup={handleCloseChooseItems}
                />
            )}

            {selectedTemplateItem && (
                <TemplateItemCUPopup
                    isOpen={selectedTemplateItem ? true : false}
                    selectedItem={selectedTemplateItem}
                    canSelectItemGroups={values?.templateItemGroups || []}
                    handleConfirmCUTemplateItem={handleConfirmCUTemplateItem}
                    handleCloseCUTemplateItem={handleCloseCUTemplateItem}
                />
            )}
        </>
    );
}


export default memo(observer(TemplateItemTableV2));