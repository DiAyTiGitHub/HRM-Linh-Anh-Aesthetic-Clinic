import { Checkbox, Chip, IconButton, Paper, TextField, makeStyles } from "@material-ui/core";
import Collapse from "@material-ui/core/Collapse";
import { alpha, withStyles } from "@material-ui/core/styles";
import ArrowDropDownIcon from "@material-ui/icons/ArrowDropDown";
import ArrowRightIcon from "@material-ui/icons/ArrowRight";
import CloseIcon from "@material-ui/icons/Close";
import TreeItem from "@material-ui/lab/TreeItem";
import TreeView from "@material-ui/lab/TreeView";
import { FastField, getIn } from "formik";
import { isEqual } from "lodash";
import React, { useEffect, useRef, useState } from "react";
import { animated, useSpring } from "react-spring/web.cjs";

const PAGE_SIZE = 20;

// Styled TreeItem
const StyledTreeItem = withStyles((theme) => ({
    iconContainer: {
        "& .close": {
            opacity: 0.3,
        },
    },
    group: {
        marginLeft: 7,
        paddingLeft: 18,
        borderLeft: `1px dashed ${alpha(theme.palette.text.primary, 0.4)}`,
    },
    label: {
        padding: "4px 0",
    },
    selected: {}, // Để trống
}))((props) => <TreeItem {...props} TransitionComponent={TransitionComponent} />);

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
        position: "absolute",
        width: "100%",
        maxHeight: 300,
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
}));

// Animation for tree items
function TransitionComponent(props) {
    const style = useSpring({
        from: { opacity: 0, transform: "translate3d(20px,0,0)" },
        to: {
            opacity: props.in ? 1 : 0,
            transform: `translate3d(${props.in ? 0 : 20}px,0,0)`,
        },
    });

    return (
        <animated.div style={style}>
            <Collapse {...props} />
        </animated.div>
    );
}

const TreeAutocompletePositionSelector = (props) => {
    return (
        <FastField {...props} name={props.name} shouldUpdate={shouldComponentUpdate}>
            {({ field, meta, form }) => (
                <MyTreeAutocomplete
                    {...props}
                    field={field}
                    meta={meta}
                    setFieldValue={form.setFieldValue}
                    formik={form}
                />
            )}
        </FastField>
    );
};

function MyTreeAutocomplete({
    api,
    name,
    searchObject,
    allowLoadOptions = true,
    clearOptionOnClose = false,
    handleChange,
    field,
    meta,
    setFieldValue,
    formik,
    label,
    oldStyle = false,
    required = false,
    getOptionDisabled,
    readOnly = false,
    multiple = false,
    size = "small",
    displayName = "name",
    idField = "id",
    parentIdField = "parentId",
    placeholder = "Select a position",
    hasChild = false,
    ...otherProps
}) {
    const [page, setPage] = useState(1);
    const [options, setOptions] = useState([]);
    const [treeData, setTreeData] = useState([]);
    const [loading, setLoading] = useState(false);
    const [keyword, setKeyword] = useState("");
    const [firstLoading, setFirstLoading] = useState(true);
    const [totalPage, setTotalPage] = useState(1);
    const [open, setOpen] = useState(false);
    const [t, setT] = useState(null);
    const [expanded, setExpanded] = useState([]);
    const [selectedItems, setSelectedItems] = useState(field.value || (multiple ? [] : null));
    const [inputValue, setInputValue] = useState("");
    const containerRef = useRef(null);

    const classes = useStyles();

    const getAllNodeIds = (nodes) => {
        let ids = [];
        nodes.forEach((node) => {
            ids.push(node[idField].toString());
            if (node.children && node.children.length > 0) {
                ids = [...ids, ...getAllNodeIds(node.children)];
            }
            // Include position IDs if it's a department
            if (node.positions && node.positions.length > 0) {
                node.positions.forEach((position) => {
                    ids.push(position[idField].toString());
                });
            }
        });
        return ids;
    };

    // Effect to fetch data when the autocomplete opens or search terms change
    useEffect(() => {
        if (open && allowLoadOptions) {
            getData();
        }
    }, [keyword, open]);

    // Add this useEffect to expand all nodes when options change
    useEffect(() => {
        if (options.length > 0) {
            const allNodeIds = getAllNodeIds(treeData);
            setExpanded(allNodeIds);
        }
    }, [options, treeData]);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (containerRef.current && !containerRef.current.contains(event.target)) {
                handleClose();
            }
            setInputValue("");
            setKeyword("");
        };

        if (open) {
            document.addEventListener("mousedown", handleClickOutside);
        } else {
            document.removeEventListener("mousedown", handleClickOutside);
        }

        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, [open]);

    // Fetch data from API
    const getData = () => {
        setLoading(true);
        let newPage = 1;
        setPage(newPage);
        api({
            ...searchObject,
            pageIndex: newPage,
            pageSize: PAGE_SIZE,
            keyword,
        })
            .then((response) => {
                if (response && response.data) {
                    setOptions(response.data.content || []);
                    setTotalPage(response.data.totalPages || 1);
                }
            })
            .catch((err) => {
                console.error("Error fetching data:", err);
            })
            .finally(() => {
                setLoading(false);
                setFirstLoading(false);
            });
    };

    // Load more results when scrolling
    const loadMoreResults = () => {
        if (page >= totalPage || loading) return;
        setLoading(true);
        const nextPage = page + 1;
        setPage(nextPage);
        api({
            ...searchObject,
            pageIndex: nextPage,
            pageSize: PAGE_SIZE,
            keyword,
        })
            .then((response) => {
                if (response && response.data && response.data.content) {
                    setOptions((prev) => [...prev, ...response.data.content]);
                    setTotalPage(response.data.totalPages);
                }
            })
            .catch((err) => {
                console.error("Error loading more results:", err);
            })
            .finally(() => {
                setLoading(false);
            });
    };

    // Handle scroll event to load more data
    const handleScroll = (event) => {
        const listboxNode = event.currentTarget;
        const position = listboxNode.scrollTop + listboxNode.clientHeight;
        if (listboxNode.scrollHeight - position <= 8 && page < totalPage) {
            loadMoreResults();
        }
    };

    // Handle opening the dropdown
    const handleOpen = () => {
        if (readOnly) return;
        setOpen(true);
        if (firstLoading && allowLoadOptions) {
            getData();
        }
    };

    // Handle closing the dropdown
    const handleClose = () => {
        setOpen(false);
        setInputValue("");
        if (clearOptionOnClose) {
            setOptions([]);
            setTotalPage(1);
        }
    };

    // Handle text input changes for search
    const handleChangeText = (value) => {
        if (readOnly) return;
        if (t) clearTimeout(t);
        setInputValue(value);
        setT(
            setTimeout(() => {
                setKeyword(value);
            }, 500)
        );
    };

    // Convert flat list to tree structure
    const buildTree = (flatList) => {
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
    };

    // Handle tree node expansion/collapse
    const handleNodeToggle = (event, nodeIds) => {
        // Chỉ cho phép đóng/mở khi click vào icon, không ảnh hưởng khi chọn node
        if (event.target.closest(".MuiTreeItem-iconContainer")) {
            setExpanded(nodeIds);
        }
    };

    const handleNodeSelect = (event, nodeId) => {
        // Skip handling if this is a department node or read-only
        if (readOnly) {
            return;
        }

        // Kiểm tra nếu click xảy ra trên icon hoặc các phần tử không phải label
        const iconContainer = event.target.closest(".MuiTreeItem-iconContainer");
        const collapseIcon = event.target.closest(".MuiTreeItem-collapseIcon");
        const expandIcon = event.target.closest(".MuiTreeItem-expandIcon");
        const endIcon = event.target.closest(".MuiTreeItem-endIcon");

        if (iconContainer || collapseIcon || expandIcon || endIcon) {
            return;
        }

        // Tìm node được chọn
        const selectedNode = findNodeById(treeData, nodeId);
        if (!selectedNode) return;

        // Kiểm tra xem đó có phải là department hay không
        const isDepartment = selectedNode.hasOwnProperty("positions");
        if (isDepartment) {
            // Nếu là department, không cho phép chọn
            return;
        }

        // Giữ node cha luôn mở sau khi chọn
        if (!expanded.includes(nodeId)) {
            setExpanded((prev) => [...prev, nodeId]);
        }

        if (multiple) {
            const isAlreadySelected = selectedItems.some((item) => item[idField] === selectedNode[idField]);
            const newSelectedItems = isAlreadySelected
                ? selectedItems.filter((item) => item[idField] !== selectedNode[idField])
                : [...selectedItems, selectedNode];

            // Chỉ cập nhật nếu có thay đổi thực sự
            if (!isEqual(selectedItems, newSelectedItems)) {
                setSelectedItems(newSelectedItems);
                setFieldValue(name, newSelectedItems);
                if (handleChange) {
                    handleChange(event, newSelectedItems);
                }
            }
        } else {
            // Xử lý chọn đơn: nếu click vào item đã chọn thì bỏ chọn
            const isAlreadySelected = selectedItems && selectedItems[idField] === selectedNode[idField];

            if (isAlreadySelected) {
                // Bỏ chọn
                setSelectedItems(null);
                setFieldValue(name, null);
                if (handleChange) {
                    handleChange(event, null);
                }
            } else {
                // Chọn item mới
                setSelectedItems(selectedNode);
                setFieldValue(name, selectedNode);
                if (handleChange) {
                    handleChange(event, selectedNode);
                }
            }
            setOpen(false);
        }
    };

    // Find a node by ID in the tree
    const findNodeById = (nodes, id) => {
        if (!id) return null;

        for (const node of nodes) {
            if (node[idField].toString() === id.toString()) {
                return node;
            }

            // Check positions if this is a department
            if (node.positions) {
                const position = node.positions.find((pos) => pos[idField].toString() === id.toString());
                if (position) return position;
            }

            if (node.children && node.children.length > 0) {
                const found = findNodeById(node.children, id);
                if (found) return found;
            }
        }
        return null;
    };

    // Render tree items recursively
    const renderTreeItems = (nodes) => {
        return nodes.map((node) => {
            const isDepartment = node.hasOwnProperty("positions"); // Kiểm tra nếu là phòng ban

            return (
                <StyledTreeItem
                    key={node[idField]}
                    nodeId={node[idField].toString()}
                    label={
                        isDepartment ? (
                            // Department label - no checkbox, styled differently
                            <div className={classes.departmentLabel}>
                                {node[displayName]}
                                {node?.numberOfPositions > 0 && (
                                    <Chip
                                        label={`${node?.numberOfPositions} vị trí`}
                                        size='small'
                                        style={{
                                            marginLeft: 8,
                                            backgroundColor: "#f5f5f5",
                                            color: "#555",
                                            fontSize: "0.75rem",
                                        }}
                                    />
                                )}
                            </div>
                        ) : (
                            // Position item - with checkbox
                            <div style={{ display: "flex", alignItems: "center" }}>
                                <Checkbox
                                    edge='start'
                                    checked={
                                        multiple
                                            ? selectedItems.some((item) => item[idField] === node[idField])
                                            : selectedItems && selectedItems[idField] === node[idField]
                                    }
                                    tabIndex={-1}
                                    disableRipple
                                    style={{ padding: "4px", margin: 0 }}
                                    color='primary'
                                    onClick={(e) => handleNodeSelect(e, node[idField])}
                                />
                                <span
                                    style={{
                                        display: "inline-block",
                                        width: "100%",
                                        borderRadius: 4,
                                        padding: "2px 4px",
                                        marginLeft: 4,
                                    }}>
                                    {node[displayName]}
                                </span>
                            </div>
                        )
                    }>
                    {isDepartment &&
                        node.positions &&
                        node.positions.map((position) => (
                            <StyledTreeItem
                                key={position[idField]}
                                nodeId={position[idField].toString()}
                                label={
                                    <div style={{ display: "flex", alignItems: "center" }}>
                                        <Checkbox
                                            edge='start'
                                            checked={
                                                multiple
                                                    ? selectedItems.some((item) => item[idField] === position[idField])
                                                    : selectedItems && selectedItems[idField] === position[idField]
                                            }
                                            tabIndex={-1}
                                            disableRipple
                                            style={{ padding: "4px", margin: 0 }}
                                            color='primary'
                                            onClick={(e) => handleNodeSelect(e, position[idField])}
                                        />
                                        <span
                                            style={{
                                                display: "inline-block",
                                                width: "100%",
                                                borderRadius: 4,
                                                padding: "2px 4px",
                                                marginLeft: 4,
                                            }}>
                                            {position[displayName]}
                                        </span>
                                    </div>
                                }
                                disabled={getOptionDisabled ? getOptionDisabled(position) : false}
                            />
                        ))}

                    {/* Render children departments if any */}
                    {node?.children && node?.children?.length > 0 && renderTreeItems(node.children)}
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

    // Delete a selected item (for multiple selection)
    const handleDeleteItem = (itemToDelete) => {
        if (readOnly) return;
        const newSelectedItems = selectedItems.filter((item) => item[idField] !== itemToDelete[idField]);
        setSelectedItems(newSelectedItems);
        setFieldValue(name, newSelectedItems);
        if (handleChange) {
            handleChange(null, newSelectedItems);
        }
    };

    const isError = meta && meta.touched && meta.error;

    useEffect(() => {
        setTreeData(hasChild ? options : buildTree(options));
    }, [hasChild, options]);

    const handleClearAll = () => {
        if (readOnly) return;

        if (multiple) {
            setSelectedItems([]);
            setFieldValue(name, []);
            if (handleChange) handleChange(null, []);
        } else {
            setSelectedItems(null);
            setFieldValue(name, null);
            if (handleChange) handleChange(null, null);
        }

        setInputValue("");
        setKeyword("");
    };

    return (
        <div
            ref={containerRef}
            className={`${classes.container} ${oldStyle ? "" : "input-container"} ${readOnly ? classes.readOnly : ""}`}>
            {label && (
                <label htmlFor={name} className={oldStyle ? "old-label" : "label-container"}>
                    {label}
                    {required && <span style={{ color: "red" }}> * </span>}
                </label>
            )}

            <div className={classes.inputContainer}>
                <TextField
                    id={name}
                    variant={otherProps?.variant || "outlined"}
                    fullWidth
                    placeholder={placeholder}
                    value={inputValue}
                    onClick={handleOpen}
                    onChange={(e) => handleChangeText(e.target.value)}
                    autoComplete='off'
                    size={size}
                    InputProps={{
                        readOnly: readOnly,
                        // Chỉ hiển thị selected item trong TextField khi không phải multiple
                        startAdornment:
                            !multiple && selectedItems ? (
                                <Chip
                                    label={selectedItems[displayName]}
                                    className={classes.chip}
                                    onDelete={
                                        readOnly
                                            ? undefined
                                            : () => {
                                                  setSelectedItems(null);
                                                  setFieldValue(name, null);
                                                  if (handleChange) handleChange(null, null);
                                              }
                                    }
                                    disabled={readOnly}
                                    size='small'
                                />
                            ) : null,
                    }}
                    error={Boolean(isError)}
                    helperText={isError ? meta.error : ""}
                    className={readOnly ? classes.readOnly : ""}
                />
                {!readOnly && (
                    <IconButton
                        style={{
                            position: "absolute",
                            right: 8,
                            top: "50%",
                            transform: "translateY(-50%)",
                            zIndex: 1,
                        }}
                        disabled={readOnly || (!multiple && !selectedItems) || (multiple && selectedItems.length === 0)}
                        onClick={handleClearAll}>
                        <CloseIcon />
                    </IconButton>
                )}
            </div>

            {/* Chỉ hiển thị danh sách Chip khi ở chế độ multiple và có items được chọn */}
            {multiple && selectedItems && selectedItems.length > 0 && (
                <div className={classes.chipsContainer}>
                    <div style={{ display: "flex", flexWrap: "wrap", gap: 4, marginTop: 8 }}>
                        {selectedItems.map((item) => (
                            <Chip
                                key={item[idField]}
                                label={item[displayName]}
                                className={classes.chip}
                                onDelete={readOnly ? undefined : () => handleDeleteItem(item)}
                                disabled={readOnly}
                                size='small'
                            />
                        ))}
                    </div>
                </div>
            )}

            {open && (
                <Paper className={classes.treeViewContainer}>
                    {loading && options.length === 0 ? (
                        <div className={classes.loading}>Loading...</div>
                    ) : options.length > 0 ? (
                        <div onScroll={handleScroll} style={{ maxHeight: 300, overflow: "auto" }}>
                            <TreeView
                                className={classes.treeViewRoot}
                                defaultCollapseIcon={<ArrowDropDownIcon />}
                                defaultExpandIcon={<ArrowRightIcon />}
                                expanded={expanded}
                                selected={getSelectedTreeIds()}
                                onNodeToggle={handleNodeToggle}
                                onNodeSelect={handleNodeSelect}
                                multiSelect={multiple}>
                                {renderTreeItems(treeData)}
                            </TreeView>
                        </div>
                    ) : (
                        <div className={classes.noOptions}>No options available</div>
                    )}
                </Paper>
            )}
        </div>
    );
}

const shouldComponentUpdate = (nextProps, currentProps) => {
    return (
        nextProps.name !== currentProps.name ||
        nextProps.value !== currentProps.value ||
        nextProps.onChange !== currentProps.onChange ||
        nextProps.label !== currentProps.label ||
        nextProps.required !== currentProps.required ||
        nextProps.disabled !== currentProps.disabled ||
        nextProps.readOnly !== currentProps.readOnly ||
        nextProps.api !== currentProps.api ||
        !isEqual(nextProps.searchObject, currentProps.searchObject) ||
        nextProps.formik.isSubmitting !== currentProps.formik.isSubmitting ||
        getIn(nextProps.formik.values, currentProps.name) !== getIn(currentProps.formik.values, currentProps.name) ||
        getIn(nextProps.formik.errors, currentProps.name) !== getIn(currentProps.formik.errors, currentProps.name) ||
        getIn(nextProps.formik.touched, currentProps.name) !== getIn(currentProps.formik.touched, currentProps.name)
    );
};

export default React.memo(TreeAutocompletePositionSelector);
