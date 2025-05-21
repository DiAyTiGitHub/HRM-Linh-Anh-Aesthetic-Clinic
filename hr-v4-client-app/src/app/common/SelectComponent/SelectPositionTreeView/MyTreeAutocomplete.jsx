import { Checkbox, Chip, IconButton, Paper, TextField, Tooltip, makeStyles } from "@material-ui/core";
import ArrowDropDownIcon from "@material-ui/icons/ArrowDropDown";
import ArrowRightIcon from "@material-ui/icons/ArrowRight";
import CloseIcon from "@material-ui/icons/Close";
import TreeView from "@material-ui/lab/TreeView";
import { useFormikContext } from "formik";
import { isEqual } from "lodash";
import React, { useCallback, useEffect, useRef, useState } from "react";
import StyledTreeItem from "./StyledTreeItem";

const PAGE_SIZE = 10;

const useStyles = makeStyles((theme) => ({
    container: {
        position: "relative",
        width: "100%",
    },
    inputContainer: {
        position: "relative",
        overflow: "hidden",
        "& .MuiOutlinedInput-root": {
            paddingTop: 8,
            paddingBottom: 8,
            maxHeight: 120,
        },
        "& .MuiAutocomplete-inputRoot": {
            paddingTop: "4px !important",
            paddingBottom: "4px !important",
        },
    },
    treeViewRoot: {
        padding: theme.spacing(1),
    },
    treeViewContainer: {
        padding: "16px",
        position: "absolute",
        width: "100%",
        maxHeight: 400,
        border: `1px solid ${theme.palette.divider}`,
        borderRadius: 4,
        marginTop: 4,
        boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
        backgroundColor: theme.palette.background.paper,
        zIndex: 1000,
        top: "100%",
        left: 0,
    },
    chip: {
        margin: 2,
    },
    popperContainer: {
        width: "100%",
        zIndex: 1300,
    },
    noOptions: {
        padding: theme.spacing(2),
        textAlign: "center",
        color: theme.palette.text.secondary,
    },
    loading: {
        padding: theme.spacing(2),
        textAlign: "center",
        color: theme.palette.text.secondary,
    },
    readOnly: {
        "& .MuiInputBase-root": {
            backgroundColor: "rgba(0, 0, 0, 0.02)",
            color: "rgba(0, 0, 0, 0.87)",
            cursor: "default",
        },
        "& .MuiOutlinedInput-notchedOutline": {
            borderColor: "rgba(0, 0, 0, 0.23)",
        },
    },
    departmentLabel: {
        fontWeight: "bold",
        color: theme.palette.text.primary,
        padding: "6px 0",
    },
    closeButton: {
        position: "absolute",
        right: theme.spacing(1),
        top: 0,
        color: theme.palette.grey[500],
    },
}));

///
const MyTreeAutocomplete = ({
    api,
    name,
    searchObject,
    allowLoadOptions = true,
    clearOptionOnClose = false,
    handleChange,
    field,
    meta,
    label,
    required = false,
    readOnly = false,
    multiple = false,
    displayName = "name",
    idField = "id",
    parentIdField = "parentId",
    placeholder = "Select a position",
    hasChild = false,
    ...otherProps
}) => {
    const debounceTimerRef = useRef(null);
    const prevKeywordRef = useRef("");
    const isInitialMount = useRef(true);
    const classes = useStyles();
    const { setFieldValue } = useFormikContext();
    const containerRef = useRef(null);

    // State management
    const [state, setState] = useState({
        page: 1,
        options: [],
        treeData: [],
        loading: false,
        keyword: "",
        firstLoading: true,
        totalPage: 1,
        open: false,
        expanded: [],
        selectedItems: field.value || (multiple ? [] : null),
        inputValue: "",
    });

    // Derived state
    const { options, treeData, expanded, selectedItems, inputValue, open, loading } = state;

    // Memoized functions
    const updateState = useCallback((updates) => {
        setState((prev) => ({ ...prev, ...updates }));
    }, []);

    const getAllNodeIds = useCallback(
        (nodes) => {
            let ids = [];
            nodes.forEach((node) => {
                ids.push(node[idField].toString());
                if (node.children?.length > 0) {
                    ids = [...ids, ...getAllNodeIds(node.children)];
                }
                if (node.positions?.length > 0) {
                    node.positions.forEach((position) => {
                        ids.push(position[idField].toString());
                    });
                }
            });
            return ids;
        },
        [idField]
    );

    const findNodeById = useCallback(
        (nodes, id) => {
            if (!id) return null;
            for (const node of nodes) {
                if (node[idField].toString() === id.toString()) return node;
                if (node.positions) {
                    const position = node.positions.find((pos) => pos[idField].toString() === id.toString());
                    if (position) return position;
                }
                if (node.children?.length > 0) {
                    const found = findNodeById(node.children, id);
                    if (found) return found;
                }
            }
            return null;
        },
        [idField]
    );

    const buildTree = useCallback(
        (flatList) => {
            const map = {};
            const roots = [];
            flatList.forEach((item) => {
                map[item[idField]] = { ...item, children: [] };
            });
            flatList.forEach((item) => {
                if (item[parentIdField] && map[item[parentIdField]]) {
                    map[item[parentIdField]].children.push(map[item[idField]]);
                } else {
                    roots.push(map[item[idField]]);
                }
            });
            return roots;
        },
        [idField, parentIdField]
    );

    // API calls
    const getData = useCallback(async () => {
        updateState({ loading: true });
        try {
            const response = await api({
                ...searchObject,
                pageIndex: 1,
                pageSize: PAGE_SIZE,
                keyword: state.keyword,
            });
            if (response?.data) {
                updateState({
                    options: response.data.content || [],
                    totalPage: response.data.totalPages || 1,
                    page: 1,
                });
            }
        } catch (err) {
            console.error("Error fetching data:", err);
        } finally {
            updateState({ loading: false, firstLoading: false });
        }
    }, [api, searchObject, state.keyword, updateState]);

    const loadMoreResults = useCallback(async () => {
        if (state.page >= state.totalPage || state.loading) return;
        updateState({ loading: true });
        try {
            const response = await api({
                ...searchObject,
                pageIndex: state.page + 1,
                pageSize: PAGE_SIZE,
                keyword: state.keyword,
            });
            if (response?.data?.content) {
                updateState((prev) => ({
                    options: [...prev.options, ...response.data.content],
                    totalPage: response.data.totalPages,
                    page: prev.page + 1,
                }));
            }
        } catch (err) {
            console.error("Error loading more results:", err);
        } finally {
            updateState({ loading: false });
        }
    }, [api, searchObject, state.page, state.totalPage, state.keyword, updateState]);

    // Event handlers
    const handleScroll = useCallback(
        (event) => {
            const { scrollTop, clientHeight, scrollHeight } = event.currentTarget;
            if (scrollHeight - (scrollTop + clientHeight) <= 8 && state.page < state.totalPage) {
                loadMoreResults();
            }
        },
        [loadMoreResults, state.page, state.totalPage]
    );

    const handleOpen = useCallback(() => {
        if (readOnly) return;
        updateState({ open: true });
        if (state.firstLoading && allowLoadOptions) getData();
    }, [readOnly, state.firstLoading, allowLoadOptions, getData, updateState]);

    const handleClose = useCallback(() => {
        updateState({
            open: false,
            inputValue: "",
            ...(clearOptionOnClose && { options: [], totalPage: 1 }),
        });
    }, [clearOptionOnClose, updateState]);

    // Xử lý thay đổi text input với debounce
    const handleChangeText = useCallback(
        (value) => {
            if (readOnly) return;

            updateState({ inputValue: value });

            // Clear timeout cũ nếu có
            if (debounceTimerRef.current) {
                clearTimeout(debounceTimerRef.current);
            }

            // Chỉ gọi API khi:
            // 1. Là chuỗi rỗng (xóa hết)
            // 2. Hoặc có ít nhất 2 ký tự
            // 3. Và giá trị khác với giá trị trước đó
            if ((value === "" || value.length >= 2) && value !== prevKeywordRef.current) {
                debounceTimerRef.current = setTimeout(() => {
                    prevKeywordRef.current = value;
                    updateState({ keyword: value });
                }, 500);
            }
        },
        [readOnly, updateState]
    );

    const handleNodeToggle = useCallback(
        (event, nodeIds) => {
            if (event.target.closest(".MuiTreeItem-iconContainer")) {
                updateState({ expanded: nodeIds });
            }
        },
        [updateState]
    );

    const handleNodeSelect = useCallback(
        (event, nodeId) => {
            if (readOnly) return;
            const iconContainer = event.target.closest(
                ".MuiTreeItem-iconContainer,.MuiTreeItem-collapseIcon,.MuiTreeItem-expandIcon,.MuiTreeItem-endIcon"
            );
            if (iconContainer) return;

            const selectedNode = findNodeById(treeData, nodeId);
            if (!selectedNode || selectedNode.positions) return;

            if (!expanded.includes(nodeId)) {
                updateState((prev) => ({ expanded: [...prev.expanded, nodeId] }));
            }

            const isAlreadySelected = multiple
                ? selectedItems.some((item) => item[idField] === selectedNode[idField])
                : selectedItems?.[idField] === selectedNode[idField];

            const newSelectedItems = multiple
                ? isAlreadySelected
                    ? selectedItems.filter((item) => item[idField] !== selectedNode[idField])
                    : [...selectedItems, selectedNode]
                : isAlreadySelected
                ? null
                : selectedNode;

            if (!isEqual(selectedItems, newSelectedItems)) {
                updateState({ selectedItems: newSelectedItems });
                setFieldValue(name, newSelectedItems);
                handleChange?.(event, newSelectedItems);
                if (!multiple) updateState({ open: false });
            }
        },
        [
            readOnly,
            treeData,
            expanded,
            multiple,
            selectedItems,
            idField,
            findNodeById,
            updateState,
            setFieldValue,
            name,
            handleChange,
        ]
    );

    // Effects
    useEffect(() => {
        if (options.length > 0) {
            updateState({ expanded: getAllNodeIds(treeData) });
        }
    }, [options, treeData, getAllNodeIds, updateState]);

    useEffect(() => {
        updateState({ treeData: hasChild ? options : buildTree(options) });
    }, [hasChild, options, buildTree, updateState]);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (containerRef.current && !containerRef.current.contains(event.target)) {
                handleClose();
            }
        };
        if (open) document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, [open, handleClose]);

    useEffect(() => {
        return () => {
            if (debounceTimerRef.current) {
                clearTimeout(debounceTimerRef.current);
            }
        };
    }, []);
    // Effect để gọi API khi keyword thay đổi
    useEffect(() => {
        // Bỏ qua lần mount đầu tiên
        if (isInitialMount.current) {
            isInitialMount.current = false;
            return;
        }

        // Chỉ gọi API khi dropdown đang mở
        if (state.open) {
            getData();
        }
    }, [state.keyword, state.open, getData]);
    return (
        <div ref={containerRef} className={classes.container}>
            <TextFieldWithChips
                label={label}
                placeholder={placeholder}
                inputValue={inputValue}
                selectedItems={selectedItems}
                multiple={multiple}
                readOnly={readOnly}
                isError={Boolean(meta?.touched && meta.error)}
                helperText={meta?.error}
                required={required}
                onOpen={handleOpen}
                onChange={handleChangeText}
                name={name}
                idField={idField}
                setFieldValue={setFieldValue}
                handleChange={handleChange}
                updateState={updateState}
            />

            {open && (
                <DropdownTreeView
                    options={options}
                    treeData={treeData}
                    expanded={expanded}
                    selectedItems={selectedItems}
                    loading={loading}
                    onNodeToggle={handleNodeToggle}
                    onNodeSelect={handleNodeSelect}
                    onScroll={handleScroll}
                    multiple={multiple}
                    displayName={displayName}
                    idField={idField}
                    open={open}
                    allowLoadOptions={allowLoadOptions}
                    getData={getData}
                    keyword={state.keyword}
                />
            )}
        </div>
    );
};

const TextFieldWithChips = React.memo(
    ({
        label,
        placeholder,
        inputValue,
        selectedItems,
        multiple,
        readOnly,
        isError,
        helperText,
        required,
        onOpen,
        onChange,
        name,
        idField,
        setFieldValue,
        handleChange,
        updateState,
    }) => {
        const classes = useStyles();
        const handleDeleteItem = useCallback(
            (itemToDelete) => {
                if (readOnly) return;
                const newSelectedItems = selectedItems.filter((item) => item[idField] !== itemToDelete[idField]);
                updateState({ selectedItems: newSelectedItems });
                setFieldValue(name, newSelectedItems);
                handleChange?.(null, newSelectedItems);
            },
            [readOnly, selectedItems, idField, updateState, setFieldValue, name, handleChange]
        );

        const onClearAll = useCallback(() => {
            if (readOnly) return;

            // Nếu không có text, xóa cả lựa chọn
            const newSelectedItems = multiple ? [] : null;
            updateState({
                selectedItems: newSelectedItems,
                inputValue: "",
                keyword: "",
            });
            setFieldValue(name, newSelectedItems);
            handleChange?.(null, newSelectedItems);
        }, [readOnly, multiple, updateState, setFieldValue, name, handleChange, inputValue]);
        const onClearText = () => {
            if (readOnly) return;
            updateState({
                inputValue: "",
                keyword: "",
            });
        };

        const onClearData = () => {
            if (readOnly) return;
            const newSelectedItems = multiple ? [] : null;
            updateState({
                selectedItems: newSelectedItems,
                inputValue: "",
                keyword: "",
            });
            setFieldValue(name, newSelectedItems);
            handleChange?.(null, newSelectedItems);
        };
        return (
            <>
                {label && (
                    <label>
                        {label}
                        {required && <span style={{ color: "red" }}> * </span>}
                    </label>
                )}
                <div className={classes.inputContainer}>
                    <TextField
                        variant='outlined'
                        fullWidth
                        placeholder={placeholder}
                        value={inputValue}
                        onClick={onOpen}
                        onChange={(e) => onChange(e.target.value)}
                        autoComplete='off'
                        error={isError}
                        helperText={helperText}
                        InputProps={{
                            readOnly,
                            startAdornment: !multiple && selectedItems && (
                                <Chip
                                    label={selectedItems.displayName}
                                    onDelete={readOnly ? undefined : onClearAll}
                                    disabled={readOnly}
                                    size='small'
                                />
                            ),
                        }}
                    />
                    {!readOnly && (
                        <Tooltip title={"Xóa từ khóa tìm kiếm"} placement='top'>
                            <IconButton
                                aria-label='close'
                                style={{
                                    position: "absolute",
                                    right: 8,
                                    top: "50%",
                                    transform: "translateY(-50%)",
                                    zIndex: 1,
                                    padding: "4px",
                                }}
                                disabled={!inputValue}
                                onClick={onClearText}>
                                <CloseIcon />
                            </IconButton>
                        </Tooltip>
                    )}
                </div>
                {multiple && selectedItems?.length > 0 && (
                    <div style={{ display: "flex", flexWrap: "wrap", alignItems: "center", gap: 4, marginTop: 8 }}>
                        <Tooltip title={"Xóa dữ liệu"} placement='top'>
                            <IconButton
                                style={{ padding: "4px" }}
                                disabled={
                                    readOnly ||
                                    (!multiple && !selectedItems) ||
                                    (multiple && selectedItems.length === 0)
                                }
                                onClick={onClearData}>
                                <CloseIcon />
                            </IconButton>
                        </Tooltip>

                        {selectedItems.map((item) => (
                            <Chip
                                key={item.id}
                                label={`${item.name} - ${item?.staff ? item?.staff?.displayName : "Vacant"}`}
                                onDelete={readOnly ? undefined : () => handleDeleteItem(item)}
                                disabled={readOnly}
                                size='small'
                            />
                        ))}
                    </div>
                )}
            </>
        );
    },
    isEqual
);

const DropdownTreeView = React.memo(
    ({
        options,
        treeData,
        expanded,
        selectedItems,
        loading,
        onNodeToggle,
        onNodeSelect,
        onScroll,
        multiple,
        displayName,
        idField,
        open,
        allowLoadOptions,
        getData,
        keyword,
    }) => {
        console.log(idField);
        const classes = useStyles();
        const renderTreeItems = (nodes) => {
            return nodes.map((node) => {
                // Kiểm tra nếu node là phòng ban (có thuộc tính numberOfPositions)
                const isDepartment = typeof node.numberOfPositions !== "undefined";

                return (
                    <StyledTreeItem
                        key={node[idField]}
                        nodeId={node[idField].toString()}
                        label={
                            isDepartment ? (
                                <DepartmentLabel node={node} />
                            ) : (
                                <PositionItem
                                    node={node}
                                    multiple={multiple}
                                    selectedItems={selectedItems}
                                    onNodeSelect={onNodeSelect}
                                    idField={idField}
                                />
                            )
                        }>
                        {/* Nếu là phòng ban, render các vị trí */}
                        {isDepartment &&
                            node.positions?.map((position) => (
                                <StyledTreeItem
                                    key={position[idField]}
                                    nodeId={position[idField].toString()}
                                    label={
                                        <PositionItem
                                            node={position}
                                            multiple={multiple}
                                            selectedItems={selectedItems}
                                            onNodeSelect={onNodeSelect}
                                            idField={idField}
                                        />
                                    }
                                />
                            ))}

                        {/* Render các node con nếu có */}
                        {node.children && renderTreeItems(node.children)}
                    </StyledTreeItem>
                );
            });
        };
        // Get array of selected node IDs for tree view
        const getSelectedTreeIds = () => {
            if (!selectedItems) return [];
            if (multiple) {
                return selectedItems.map((item) => item[idField].toString());
            }
            return [selectedItems[idField]?.toString()];
        };
        return (
            <Paper className={classes.treeViewContainer}>
                {loading && options.length === 0 ? (
                    <div>Loading...</div>
                ) : options.length > 0 ? (
                    <div onScroll={onScroll} style={{ maxHeight: 300, overflow: "auto" }}>
                        <TreeView
                            defaultCollapseIcon={<ArrowDropDownIcon />}
                            defaultExpandIcon={<ArrowRightIcon />}
                            expanded={expanded}
                            selected={getSelectedTreeIds(selectedItems, idField, multiple)}
                            onNodeToggle={onNodeToggle}
                            onNodeSelect={onNodeSelect}
                            multiSelect={multiple}>
                            {renderTreeItems(treeData)}
                        </TreeView>
                    </div>
                ) : (
                    <div>No options available</div>
                )}
            </Paper>
        );
    },
    (prevProps, nextProps) => {
        // Chỉ re-render khi có thay đổi liên quan đến props quan trọng
        return (
            prevProps.options === nextProps.options &&
            prevProps.treeData === nextProps.treeData &&
            prevProps.expanded === nextProps.expanded &&
            isEqual(prevProps.selectedItems, nextProps.selectedItems) &&
            prevProps.loading === nextProps.loading
        );
    }
);

const DepartmentLabel = React.memo(({ node }) => (
    <div>
        <strong>{node.name}</strong>
        {node.numberOfPositions >= 0 && (
            <Chip
                label={`${node.numberOfPositions} vị trí`}
                size='small'
                style={{ marginLeft: 8, backgroundColor: "#f5f5f5", color: "#555", fontSize: "0.75rem" }}
            />
        )}
    </div>
));

const PositionItem = React.memo(
    ({ node, multiple, selectedItems, onNodeSelect, idField }) => {
        console.log(idField);
        const isChecked = multiple
            ? selectedItems.some((item) => item[idField] === node[idField])
            : selectedItems?.[idField] === node[idField];
        return (
            <div style={{ display: "flex", alignItems: "center" }}>
                <Checkbox
                    edge='start'
                    checked={isChecked}
                    tabIndex={-1}
                    disableRipple
                    color='primary'
                    style={{ padding: "4px", margin: 0 }}
                    onClick={(e) => onNodeSelect(e, node.id)}
                />
                <span style={{ marginLeft: 4 }}>
                    {node.name} - {node?.staff ? node?.staff?.displayName : "Vacant"}
                </span>
            </div>
        );
    },
    (prevProps, nextProps) => {
        return (
            prevProps.node.id === nextProps.node.id &&
            isEqual(prevProps.selectedItems, nextProps.selectedItems) &&
            prevProps.multiple === nextProps.multiple
        );
    }
);

export default React.memo(MyTreeAutocomplete);
