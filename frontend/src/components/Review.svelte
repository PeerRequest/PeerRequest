<script>
    import {BreadcrumbItem, TableBodyCell, TableBodyRow} from "flowbite-svelte";
    import Skeleton from "svelte-skeleton-loader"
    import {onMount} from "svelte";

    export let error;
    export let review;
    export let category = null;

    //TODO: UserController

    export let paper = null;
    export let loading = false;

    export let show_category = false;
    export let show_paper = false;
    export let show_reviewer = false;


    let users = null;
    let reviewer = null;

    function loadReviewer() {
        loadUsers();
        if (users === !null) {
            reviewer = users.find(user => user.id === review.reviewer_id)
        } else {
            console.log("No users could be found.")
        }

    }

    function loadUsers() {
        users = null;
        fetch("/api/users")
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    users = resp.content;
                }
            })
            .catch(err => console.log(err))
    }

    onMount(() => {
        loadReviewer();
    });

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
        <TableBodyCell>
            <BreadcrumbItem href="/categories/{category.id}/entries/{paper.id}/reviews/{review.id}">Review
                #{review.id}</BreadcrumbItem>
        </TableBodyCell>

        {#if show_reviewer}
            <TableBodyCell>
                <BreadcrumbItem>
                    {reviewer === !null ? ((reviewer.name === !null) ? reviewer.name : "undefined") : "no reviewer"}
                </BreadcrumbItem>
            </TableBodyCell>
        {/if}

        {#if show_paper}
            <TableBodyCell>
                <BreadcrumbItem href="/categories/{paper.category_id}/entries/{paper.id}">{paper.name}</BreadcrumbItem>
            </TableBodyCell>
        {/if}

        {#if show_category}
            <TableBodyCell>
                <BreadcrumbItem href="/categories/{category.id}">{category.name}</BreadcrumbItem>
            </TableBodyCell>
        {/if}
    {/if}

</TableBodyRow>