<script>
    import {BreadcrumbItem, Button, TableBodyCell, TableBodyRow} from "flowbite-svelte";
    import Skeleton from "svelte-skeleton-loader"
    import {createEventDispatcher, onMount} from "svelte";
    import Cookies from "js-cookie";
    import {goto} from "$app/navigation";

    export let href;
    export let paper = "";
    // export let rating = null;
    export let category = null;
    export let slots = null;
    export let loading = false;
    export let current_user = null;
    export let error = null;
    export let show_category = false;
    export let show_slots = false;
    export let isReviewer = false;

    let process = null;
    let review = null;

    const dispatch = createEventDispatcher();

    function loadCategory() {
        category = null;
        fetch("/api/categories/" + paper.category_id)
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    category = resp;
                }
            })
            .catch(err => console.log(err))
    }

    function loadDirectRequestProcess() {
        process = null;
        fetch("/api/categories/" + paper.category_id + "/entries/" + paper.id + "/process")
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    process = resp;
                    slots = process.open_slots;

                }
            })
            .catch(err => console.log(err))
    }

    function claimSlot() {
        return fetch("/api/categories/" + paper.category_id + "/entries/" + paper.id + "/process/claim", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: {}
        })
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    console.log("A review slot has been claimed.")
                    loadDirectRequestProcess()
                    dispatch("claimSlot");
                }
            })
    }


    onMount(() => {
        loadCategory()
        if (show_slots) {
            loadDirectRequestProcess();
        }
        current_user = JSON.parse(Cookies.get("current-user") ?? "{}")
    })
</script>


<TableBodyRow>
    {#if loading || category === null || paper === null}
        {#each [...Array(5).keys()] as i}
            <TableBodyCell>
                <div>
                    <Skeleton/>
                </div>
            </TableBodyCell>
        {/each}
    {:else }
        <TableBodyCell>
            {#if current_user !== null && (current_user.id === paper.researcher_id || isReviewer) }
                <BreadcrumbItem href="/categories/{paper.category_id}/entries/{paper.id}">{paper.name}</BreadcrumbItem>
            {:else }
                <BreadcrumbItem>{paper.name}</BreadcrumbItem>
            {/if}
        </TableBodyCell>

        {#if show_category && category !== null}
            <TableBodyCell>
                <BreadcrumbItem
                        href="/categories/{category.id}">{category.name}</BreadcrumbItem>
            </TableBodyCell>
        {/if}

        {#if show_slots}
            {#if slots === null}
                <TableBodyCell>0</TableBodyCell>
                <TableBodyCell>
                    <Button disabled outline size="xs">Claim Review Slot</Button>
                </TableBodyCell>
            {:else}
                <TableBodyCell>{slots}</TableBodyCell>
                <TableBodyCell>
                    <Button disabled={slots<=0} outline size="xs"
                            on:click={slots > 0
                            ? () => {
                                claimSlot();
                                goto(href);
                            }
                            : null}>
                        Claim Review Slot
                    </Button>
                </TableBodyCell>
            {/if}

        {/if}

        <!--

        {#if rating !== null}
            <TableBodyCell>
                <StarRating rating={rating}/>
            </TableBodyCell>
        {/if}
        -->


    {/if}

</TableBodyRow>