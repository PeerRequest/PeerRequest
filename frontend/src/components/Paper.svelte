<script>
    import {BreadcrumbItem, Button, TableBodyCell, TableBodyRow} from "flowbite-svelte";
    import Skeleton from "svelte-skeleton-loader"
    import {createEventDispatcher, onMount} from "svelte";

    export let href;
    export let paper = "";
    // export let rating = null;
    export let slots = null;
    export let loading = false;
    export let error = null;
    export let show_category = false;
    export let show_slots = false;

    let process = null;
    let reviewer_id = null;
    let category = null;
    let reviews = null;
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
                    console.log("claimsloterror!", error);
                } else {
                    console.log("A review slot has been claimed.")
                    loadDirectRequestProcess()
                    dispatch("claimSlot");
                }
            })
            .catch(err => console.log(err));
    }

    onMount(() => {
        loadCategory()
        if (show_slots) {
            loadDirectRequestProcess();
        }
    })


</script>


<TableBodyRow>
    {#if loading}
        {#each [...Array(5).keys()] as i}
            <TableBodyCell>
                <div>
                    <Skeleton/>
                </div>
            </TableBodyCell>
        {/each}
    {:else }

        {#if category !== null}
            <TableBodyCell>
                <BreadcrumbItem href="/categories/{category.id}/entries/{paper.id}">{paper.name}</BreadcrumbItem>
            </TableBodyCell>


            {#if show_category && category !== null}
                <TableBodyCell>
                    <BreadcrumbItem
                            href="/categories/{category.id}">{category.name}</BreadcrumbItem>
                </TableBodyCell>
            {/if}

            {#if show_slots && slots !== null}
                {#if slots === null}
                    <TableBodyCell>0</TableBodyCell>
                    <Button disabled outline size="xs">Claim Review Slot</Button>
                {/if}
                <TableBodyCell>{slots}</TableBodyCell>
                <TableBodyCell>
                    <!--
                    <Button disabled={slots<=0} href={href} outline size="xs" on:click={() => claimSlot()}>
                        Claim Review Slot
                    </Button>
                    -->
                    <Button disabled={slots<=0} href={href} outline size="xs"
                            on:click={() => claimSlot()}>
                        Claim Review Slot
                    </Button>
                </TableBodyCell>
            {/if}

            <!--
            {#if rating !== null}
                <TableBodyCell>
                    <StarRating rating={rating}/>
                </TableBodyCell>

            {/if}
            -->


        {/if}

    {/if}
</TableBodyRow>